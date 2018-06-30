/*
Д/з №6 по теме "Работа в сети"
Модернизировать код SimpleClient так чтобы клиент мог не только отправлять
сообщения на сервер, но и получать их от него.

Простой чат-клиент

@author Grishin Dmitriy
@version dated 16.01.18
@link null
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleClient {
    final String SERVER_ADDR = "localhost"; // или "127.0.0.1"
    final int SERVER_PORT = 2048;//адрес порта сервера
    final String CLIENT_PROMPT = "> ";
    final String CONNECT_TO_SERVER = "Connection to server established."; //Соединение с сервером установлено
    final String CONNECT_CLOSED = "Connection closed."; //Соединение закрыто
    final String EXIT_COMMAND = "exit"; //команда для выхода

    Socket socket;
    Scanner scanner;
    PrintWriter writer;
    BufferedReader reader;
    String message;

    public static void main(String[] args) {
        new SimpleClient();
    }

    SimpleClient() {
        scanner = new Scanner(System.in);//сканер для ввода с клавиатуры
        try{
            socket = new Socket(SERVER_ADDR, SERVER_PORT);//сокет клиента
            writer = new PrintWriter(socket.getOutputStream());//исходящий поток
            System.out.println(CONNECT_TO_SERVER);//соединение установлено

            //ЧТЕНИE входящих сообщений
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do{
                System.out.print(CLIENT_PROMPT);//знак-подсказка печатать в этой строке
                message = scanner.nextLine();//считываем сообщение из консоли
                writer.println(message);//отправка сообщения серверу
                writer.flush();//сброс буфера исходящего потока

                System.out.println("Сервер: " + reader.readLine());//чтение входящего потока и вывод в консоль
            }while(!message.equalsIgnoreCase(EXIT_COMMAND));
            //закрываю ресурсы: сканер, ридер и райтер:
            writer.close();
            socket.close();
            reader.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(CONNECT_CLOSED);
    }
}
