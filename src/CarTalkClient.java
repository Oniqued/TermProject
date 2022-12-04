import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

@SuppressWarnings("removal") //JApplet은 더이상 지원하지 않음 >> Warning을 무시 
public class CarTalkClient extends JApplet implements Runnable, ActionListener { //Thread, ActionListener의 인터페이스를 참조
    DataInputStream  is;		// stream
    DataOutputStream  os;		// stream
    JTextField login, car, typein;		// 텍스트 입력을 위한 변수
    JTextArea ta;			// 텍스트 출력을 위한 변수
    JPanel portal,chatroom;		// 총 두개의 Panel이 있음. 메인 화면과 채팅방
    CardLayout cardm;		// 카드 레이아웃 매니저 
    String myname; //이름을 저장할 공간

    //초기화 하는 과정
    public void init()  {
        setGUIcards();
        setLayout(cardm = new CardLayout());
        add (portal, "card-login");			// card-1
        add (chatroom, "card-chat");			// card-2
        cardm.show (this.getContentPane(), "card-login");	// show the 1st card
    }

    //유저 인터페이스 배치
    void setGUIcards() { 
        portal = new JPanel(new BorderLayout());		// card-1
        portal.add(new JLabel(new ImageIcon("./img/cartalk.png")), "Center");  // 1-1
        JPanel logpan = new JPanel();
        login = new JTextField(20);
        car = new JTextField(20);
        login.addActionListener(this);
        logpan.add(new JLabel("소유하고 계신 차량 ")); logpan.add(car);  // 1-2.a, 1-2.b
        logpan.add(new JLabel("닉네임 ")); logpan.add(login);  // 1-2.a, 1-2.b
        portal.add(logpan, "South"); 

        chatroom = new JPanel(new BorderLayout());  	// card-2
        typein = new JTextField();
        typein.addActionListener(this);
        chatroom.add(typein, "South");	// 2-2

        ta = new JTextArea(14, 25*1);
        ta.setBackground(new Color(220,255,255));
        ta.setEditable(false);
        ta.setLineWrap(true);
        JScrollPane spane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatroom.add(spane, "Center");  // 2-1 (-> replace it with a dual interface)
        //- dual design here - 7+
    }

    public static void main(String args[]) {
        CarTalkClient chatter = new CarTalkClient(); //
        chatter.init(); //JApplet 함수
        chatter.start(); //JApplet 함수
        JFrame f = new JFrame("Lab XI. CafeGaggle");
        f.getContentPane().add(chatter);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void start() {
        System.out.println("* Thread starting *");
        (new Thread(this)).start();	// threading (서버 메시지를 받는)
    }

    public void run() {
        System.out.println("run: CarTalk 실행 ...");
        try{
            // 서버접속(create a Socket) & IO stream 두개 만들기
            Socket sock = new Socket("localhost", 2022);
            is = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            execute();
        }catch(IOException e){
            System.out.println("연결 실패");
        }
    }

    public void execute() {   // 서버에서 계속 오는 메시지를 받아서 출력한다.
        try {
            while (true) {
                // get msg from the server(thread) and display them -
                String msg = is.readUTF();
                ta.append(msg+"\n");
            }
        } catch(IOException e){} finally { System.out.println("stop"); }
    }

    public void actionPerformed (ActionEvent e) {
        Component c = (Component) e.getSource();
        // textfield 입력(이름, 채팅글)을 읽어서 서버로 보낸다.
        if ((JTextField) e.getSource() == login) {
            myname = login.getText();
            try{os.writeUTF(myname); os.flush();}
            catch (IOException ioe){System.out.println("fail");}
            cardm.show(this.getContentPane(), "card-chat");
            //focus
            typein.requestFocus();
        }
        else {  //- 챗글 from typein -
            try{os.writeUTF(typein.getText()); os.flush();}
            catch(IOException ioe){}
            typein.setText("");
        }
    }
} /* KChatClient */
