package net.hugescrub.services;

import lombok.SneakyThrows;
import net.hugescrub.models.GamesResults;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Optional;

public class Bot extends TelegramLongPollingBot {

    static final String BOT_NAME = "@bot_name";
    static final String BOT_TOKEN = "bot_token";

    /**
     * Receiving messages.
     *
     * @param update contains user message.
     */
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        // calling message handler
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }

        if(update.hasMessage() && !(update.getMessage().getText().contains("/"))){
            handleRequest(update.getMessage());
        }
    }

    /**
     * @param message Represents received user message.
     */
    @SneakyThrows
    public void handleMessage(Message message) {

        Long chatId = message.getChatId();

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
                                                "/all: Get all games available")
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
    }

    /**
     * @param message Represents received user message.
     */
    @SneakyThrows
    public void handleRequest(Message message){

        Long chatId = message.getChatId();

        if (message.getText().equals("Find by game name")) {
            execute(
                    SendMessage.builder()
                            .text("Enter game name...")
                            .chatId(chatId.toString())
                            .build()
            );
        }  else if (message.hasText()) {
            GamesResults gamesResults = TrackerParser.searchGames(message.getText());
            StringBuilder sb = new StringBuilder();

            for (String link : gamesResults.getGameLinks()){
                sb.append(link).append("\n");
            }

            for (String name : gamesResults.getGameNames()){
                sb.append(name).append("\n");
            }
            execute(
                    SendMessage.builder()
                            .text(sb.toString())
                            .chatId(chatId.toString())
                            .build());
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    public static void main(String[] args) {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
