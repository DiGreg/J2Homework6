/**
 * Java. Level 2. Lesson 6
 * Simple server for chat (Простой сервер для чата)
 *
 * @author Sergey Iryupin
 * @version 0.1 dated Jan 13, 2018
 * Rus comments by Dmitriy Grishin
 */

import java.io.*;
import java.net.*;

public class SimpleServer {
    //блок констант сервера
    final int SERVER_PORT = 2048;//адрес порта
    final String SERVER_START = "Server is started..."; //Сервер запущен
    final String SERVER_STOP = "Server stopped."; //Сервер остановлен
    final String CLIENT_JOINED = " client joined."; //-й клиент присоединился
    final String CLIENT_DISCONNECTED = " disconnected"; //отключился
    final String EXIT_COMMAND = "exit"; // команда для выхода клиента

    public static void main(String[] args) {
        new SimpleServer();
    }

    SimpleServer() {
        int clientCount = 0;//счётчик клиентов
        System.out.println(SERVER_START);//сообщение в консоль, что сервер запущен
        //В ресурсах try() создаём сокет сервера (ServerSocket is AutoCloseable) (на вебинаре был обычный try)
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = server.accept();//ожидание подключения клиента и получения ссылки на клиентский сокет
                System.out.println("#" + (++clientCount) + CLIENT_JOINED);//сообщение в консоль, какой клиент подключился
                new Thread(new ClientHandler(socket, clientCount)).start();//создаём объект - нить обработки отдельного клиента и стартуем
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(SERVER_STOP);
    }

    /**
     * ClientHandler: (ВНУТРЕННИЙ класс для нити обработки отдельного клиента)
     */
    class ClientHandler implements Runnable {
        BufferedReader reader;
        PrintWriter writer;
        Socket socket;//для запоминания клиентского сокета
        String name;//имя подключённого клиента

        //В конструктор передаём параметры - ссылку на клиентский сокет и номер клиента
        ClientHandler(Socket clientSocket, int clientCount) {
            try {
                socket = clientSocket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//обработчик ("читатель") входящего потока
                writer = new PrintWriter(socket.getOutputStream());//обработчик ("писатель") исходящего потока
                name = "Client #" + clientCount;//задаём имя клиенту
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public void run() {
            String message;
            try {
                do {
                    message = reader.readLine();//читаем сообщение от клиента
                    System.out.println(name + ": " + message);//выводим его в консоль
                    writer.println("echo: " + message);//и отправляем клиенту обратно (эхо)
                    writer.flush();//для гарантированной отправки содержимого буфера исходящего потока
                } while (!message.equalsIgnoreCase(EXIT_COMMAND));//пока сообщение не равно команде exit
                socket.close();//закрытие клиентского сокета
                System.out.println(name + CLIENT_DISCONNECTED);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
