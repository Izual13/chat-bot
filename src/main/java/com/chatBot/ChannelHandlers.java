package com.chatBot;

import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class ChannelHandlers extends TelegramLongPollingBot {

    private static final String USER_NAME = System.getProperty("USER_NAME");
    private static final String TOKEN = System.getProperty("TOKEN");

    ChannelHandlers() {
        DefaultBotOptions options = getOptions();
        options.setMaxThreads(1);
        System.out.println(options.getMaxThreads());
        System.out.println("USER_NAME: " + USER_NAME);
        System.out.println("TOKEN: " + TOKEN);
    }

    @Override public String getBotUsername() {
        return USER_NAME;
    }

    @Override public String getBotToken() {
        return TOKEN;
    }

    @Override public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        try {
            if (update.hasMessage() && message.hasText()) {
                buildMessage(message, message.getText());
            } else if (update.hasMessage() && message.hasPhoto()) {
                List<PhotoSize> photo = message.getPhoto();
                PhotoSize photoSize = photo.get(photo.size() - 1);
                parsePhoto(getFilePath(photoSize.getFileId()));
            } else if (update.hasMessage() && message.hasDocument()) {
                Document document = message.getDocument();
                parsePhoto(getFilePath(document.getFileId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return getFile(getFile).getFilePath();
    }

    private void parsePhoto(String filePath) throws Exception {
        System.out.println(filePath);

        URL url = new URL(String.format("https://api.telegram.org/file/bot%s/%s", TOKEN, filePath));

        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);


        try (InputStream inputStream = urlConnection.getInputStream()) {
//processing images
        }

    }

    private InputStream getCat() throws Exception {
        URL url = new URL("http://thecatapi.com/api/images/get?format=src&type=gif");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        return urlConnection.getInputStream();
    }

    private void buildMessage(Message message, String text) throws Exception {
        System.out.println(String.format("%s %s: %s", message.getFrom().getFirstName(), message.getFrom().getLastName(), message.getText()));
        Long chatId = message.getChatId();
        if (text.startsWith("/cat")) {
            InputStream inputStream = getCat();
            SendDocument sendPhoto = new SendDocument()
                    .setChatId(chatId)
                    .setNewDocument(String.valueOf(Math.random()) + ".gif", inputStream);
            sendDocument(sendPhoto);
            closeInputStream(inputStream);

        } else {
//            text = String.format("*\"%s\"* (c) _%s %s_", message.getText().replaceFirst("/", ""), message.getFrom().getFirstName(), message.getFrom().getLastName());
//            SendMessage sendMessage = new SendMessage()
//                    .setChatId(chatId)
//                    .enableMarkdown(true)
//                    .setText(text);
//            sendMessage(sendMessage);
        }
    }

    private void closeInputStream(InputStream inputStream) throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
