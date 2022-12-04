import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class CarTalkServer {
    protected Vector vhandler; /* 사용자 등록 장부 (Vector) */
    int id = 0;
    CarTalkServer (int port) throws IOException {
        //-- 서버소켓 생성 (챗방 오픈) --
        ServerSocket server = new ServerSocket (port);
        vhandler = new Vector (2, 5);
        System.out.println("CarTalkRoom@"+ port +" 준비 완료 ...");
        while(true){
            Socket csock = server.accept();
            CarTalkHandler c = new CarTalkHandler(this, csock);
            vhandler.addElement(c); //user등록(handler as the user)
            System.out.println(++id + "번 손님 입장(현재 " + vhandler.size() + "명)");
            c.start();
            //+ 손님 도착 -> KChatHandler 생성&응대 시작 +
        }
    }
    public int numChatters() { return vhandler.size(); }
    public static void main (String args[]) throws IOException {
        if (args.length != 1)
            throw new RuntimeException ("Syntax: KChatServer port");
        new CarTalkServer (Integer.parseInt (args[0]));
    }
}
