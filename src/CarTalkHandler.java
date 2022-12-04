import java.io.*;
import java.net.*;
import java.util.*;

class CarTalkHandler extends Thread {
    protected CarTalkServer server; //- for accessing vhandler – protected Socket sock;
    protected Socket sock;
    protected DataInputStream is;
    protected DataOutputStream os;

    CarTalkHandler(CarTalkServer server, Socket sock) throws IOException {
        this.server = server;
        this.sock = sock;
        // create I/O streasm to send & receive messags :
        is = new DataInputStream(
                new BufferedInputStream(sock.getInputStream()));
        os = new DataOutputStream(
                new BufferedOutputStream(sock.getOutputStream()));
    }

    public void run() {
        //--- try { 사용자의 login/typein text를 받아 방송한다 } catch(-){} --- }
        String name = "";
        try {
            // (1) 로그인 사용자 받음
            name = is.readUTF(); //-from login
            if (name.equals("")) name = "익명";
            // (2) 알림 - "누구" 입장
            broadcast(name + "님 입장!");
            // (3) 반복 : 이후 담당 고객의 모든 메시지를 전달 방송 :
            while (!interrupted()) {broadcast(name + ": " + is.readUTF());}
        } catch (IOException e) {
            System.out.println("접속 끊김 : ");
        } finally {
            //사용자의 연결이 끊김
            System.out.println(name + "님 퇴장");
            server.vhandler.removeElement(this);
        }
    }

    protected void broadcast(String message) {
        synchronized (server.vhandler) {
            Enumeration en = server.vhandler.elements(); // ‘순회’ 지원 객체
            while (en.hasMoreElements()) {
                CarTalkHandler c = (CarTalkHandler) en.nextElement();
                // 각 고객담당 KChatHandler 가 열어 놓은 소켓으로 msg 전달.
                try {
                    c.os.writeUTF(message);
                    c.os.flush();
                } catch (IOException e) {}
            }
        }
    }
}