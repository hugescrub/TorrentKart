package net.hugescrub.services;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;


public class FileDownloader {

    private File file;

    public SendDocument sendFile(Long chatId, String fileUrl, String fileName) throws IOException {

        SendDocument doc = new SendDocument();
        downloadFile(fileUrl, fileName);
        doc.setChatId(chatId.toString());
        doc.setDocument(new InputFile(file));
        return doc;
    }

    public void downloadFile(String url, String fileName) throws IOException {

        // do request
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", TrackerParser.USER_AGENT);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        // create file
        FileUtils.copyInputStreamToFile(entity.getContent(), file = new File(fileName));
    }
}