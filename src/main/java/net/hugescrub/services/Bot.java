package net.hugescrub.services;

import net.hugescrub.models.GamesResults;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    static final String BOT_NAME = "@bot_name";
    static final String BOT_TOKEN = "bot_token";

    /**
     * Receiving messages.
     *
     * @param update contains user message.
     */

    @Override
    public void onUpdateReceived(Update update) {

        // calling message handler
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }

        // call message handler if received message is not a command
        if (update.hasMessage() && !(update.getMessage().getText().contains("/"))) {
            handleRequest(update.getMessage());
        }

        // handle callback query
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery != null) {
                sendFileOnCallback(callbackQuery);
            }
        }
    }

    /**
     * @param callbackQuery Represents a callback query we use for getting data from button pressed.
     */

    // TODO: (Not crucial) Connect database for storing files.
    public void sendFileOnCallback(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getFrom().getId();
        String torrentFileUrl = callbackQuery.getData();
        String fileName = "result.torrent";

        try {
            FileDownloader fileDownloader = new FileDownloader();
            SendDocument sendDocument = fileDownloader.sendFile(chatId, torrentFileUrl, fileName);
            // for debug
            System.out.println("Callback data: " + callbackQuery.getData());
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message Represents received user message.
     */
    public void handleMessage(Message message) {

        Long chatId = message.getChatId();

        try {
            if (message.hasText() && message.hasEntities()) {
                Optional<MessageEntity> commandEntity =
                        message.getEntities()
                                .stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
                if (commandEntity.isPresent()) {

                    String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                    switch (command) {
                        case "/start":
                            // create button rows
                            KeyboardRow firstRow = new KeyboardRow();
                            KeyboardRow secondRow = new KeyboardRow();
                            firstRow.add("Find by game name");
                            secondRow.add("Find by genre");

                            // add buttons rows to list
                            List<KeyboardRow> buttons = List.of(firstRow, secondRow);

                            execute(
                                    SendMessage.builder()
                                            .text("Welcome! \nI can get you a torrent file for the game you need.\n\n")
                                            .chatId(chatId.toString())
                                            .replyMarkup(
                                                    ReplyKeyboardMarkup.builder()
                                                            .keyboard(buttons)
                                                            .selective(true)
                                                            .resizeKeyboard(true)
                                                            .oneTimeKeyboard(true)
                                                            .build())
                                            .build());
                            return;
                        case "/help":
                            execute(
                                    SendMessage.builder()
                                            .text("Available commands: \n\n" +
                                                    "/start: Initialize bot -> get file \n" +
                                                    "Credits:\n" +
                                                    "@hugescrub\n" +
                                                    "https://github.com/hugescrub/")
                                            .chatId(chatId.toString())
                                            .build()
                            );
                    }
                }
            }

            if (message.hasText() && message.getText().equals("Search by game name")) {
                execute(
                        SendMessage.builder()
                                .text("Enter game name...")
                                .chatId(chatId.toString())
                                .build()
                );
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message Represents received user message.
     */
    public void handleRequest(Message message) {

        Long chatId = message.getChatId();

        try {
            if (message.getText().equals("Find by game name")) {
                execute(
                        SendMessage.builder()
                                .text("Enter game name...")
                                .chatId(chatId.toString())
                                .build()
                );
            } else if (message.getText().equals("Find by genre")) {
                execute(
                        SendMessage.builder()
                                .text("Not yet done...")
                                .chatId(chatId.toString())
                                .build()
                );
            } else if (message.hasText()) {
                GamesResults gamesResults = TrackerParser.searchGames(message.getText());

                // get links and names
                List<String> links = gamesResults.getGameLinks();
                List<String> names = gamesResults.getGameNames();

                // put inside
                List<List<InlineKeyboardButton>> buttons = buttonsFromData(links, names);

                execute(
                        SendMessage.builder()
                                .text("Search results:\n")
                                .chatId(chatId.toString())
                                .replyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboard(buttons)
                                        .build())
                                .build());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param links Represents links of parsed <a/> tag elements
     * @param names Represents text of parsed <a/> tag elements
     * @return Returns inline buttons to send them as a reply for user message
     */

    public List<List<InlineKeyboardButton>> buttonsFromData(List<String> links, List<String> names) {
        // create rows and buttons
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // iterate links and names to put them in rows
        Iterator<String> linksIterator = links.listIterator();
        Iterator<String> namesIterator = names.listIterator();

        // put rows
        while (linksIterator.hasNext() && namesIterator.hasNext()) {
            int counter = 0;
            while (counter < names.size()) {
                List<InlineKeyboardButton> newButton = new ArrayList<>();
                newButton.add(InlineKeyboardButton.builder()
                        .text(namesIterator.next())
                        .callbackData(TrackerParser.getFile(linksIterator.next()))
                        .build());
                buttons.add(newButton);
                counter++;
            }
        }
        return buttons;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}