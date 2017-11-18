package ru.salad.chatgame.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.salad.chatgame.Country;
import ru.salad.chatgame.util.Config;

public class ChatBot extends TelegramLongPollingBot{
	private final String botUsername;
	private final String botToken;
	
	public ChatBot(String botUsername, String botToken) {
		this.botUsername = botUsername;
		this.botToken = botToken;
	}

	public ChatBot(Config config) {
		this.botUsername = config.getBotUsername();
		this.botToken = config.getBotToken();
	}


	@Override
	public void onUpdateReceived(Update update) {
		if(update.hasMessage()&&update.getMessage().hasText()) {
			String text = update.getMessage().getText();
			if(text.startsWith("/next")){
				InputStream is = drawSymbol(0,0," ",Color.red);
				SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText("���-1 (@username)");
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("����������� �������, \n��������� \n�����, ����� �������� - 4000 ��������");
				try {
					execute(msg);
					sendPhoto(map);
					msg.setText("��� ������ �������� ����� ������ ��������� ��������");
					execute(msg);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
				
				
			}else if(text.startsWith("/draw")&&text.contains(":")) {

				String[] data = text.split(" ")[1].split(":");
				if(data.length!=3) {
					return;
				}
					
				if(!Country.canGo(3, 3, Integer.valueOf(data[0]),Integer.valueOf(data[1]))) {
					return;
				}
				int x =  Integer.valueOf(data[0]);

				int y; //= Integer.valueOf(cords[1]);//24;
				if(x%2==0) {
					x = x*41/2+26;
					y = 44;
				}else {
					x = (x-1)/2*41+46;
					y = 32;
				}
				if(Integer.valueOf(data[1])%2==0) {
					y = y  + 47*Integer.valueOf(data[1])/2;
				}else {
					y = y + 24 + 47*(Integer.valueOf(data[1])-1)/2;
				}
				InputStream is = drawSymbol(x,y,data[2],Color.red);
				if(is == null) return;
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId());
				try {
					sendPhoto(map);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getBotToken() {
		return this.botToken;
	}

	@Override
	public String getBotUsername() {
		return this.botUsername;
	}
	
	
	private InputStream drawSymbol(int x, int y, String symbols, Color color) {
		try {
			BufferedImage img = ImageIO.read(new File("images/map_basic_num.jpg"));

			// Obtain the Graphics2D context associated with the BufferedImage.
			Graphics2D g = img.createGraphics();
			g.setColor(color);
			// Draw on the BufferedImage via the graphics context.
			//g.drawOval(50, 50, 12, 12);

			//g.drawLine(42, 14, 80, 80);
			//g.drawLine(80, 80, 118, 145);
			//g.drawString(symbols,x,y);
			g.drawLine(x, y, x+10, y+10);
		// Clean up -- dispose the graphics context that was created.
			g.dispose();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			ImageIO.write(img, "jpg", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;
			
		}catch(IOException e){
			e.printStackTrace();
			
			return null;
		}
	}
}
