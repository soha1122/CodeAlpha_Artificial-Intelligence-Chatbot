import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.util.*;

public class ChatbotGUI {

    static JPanel chatPanel;
    static JScrollPane scrollPane;
    static JTextField inputField;

    static Map<String,String> memory = new HashMap<>();
    static int messageCount = 0;
    static String lastIntent = "";

    static JFrame frame;

    public static void main(String[] args){
        showLogin();
    }

    // ✅ LOGIN SCREEN
    static void showLogin(){

        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(400,300);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.getContentPane().setBackground(new Color(10,25,47));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial",Font.BOLD,22));

        JTextField username = new JTextField(15);
        JPasswordField password = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");

        gbc.gridy=0;
        loginFrame.add(title,gbc);

        gbc.gridy=1;
        loginFrame.add(username,gbc);

        gbc.gridy=2;
        loginFrame.add(password,gbc);

        gbc.gridy=3;
        loginFrame.add(loginBtn,gbc);

        loginBtn.addActionListener(e->{
            String user = username.getText();
            String pass = new String(password.getPassword());

            if(user.equals("admin") && pass.equals("123")){
                loginFrame.dispose();
                memory.put("name", user);
                showChatbot();
            }else{
                JOptionPane.showMessageDialog(loginFrame,"Invalid Login");
            }
        });

        loginFrame.setVisible(true);
    }

    static void showChatbot(){

        frame = new JFrame("Advanced AI Chatbot");
        frame.setSize(450,650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Color bg = new Color(10,25,47);
        Color userColor = new Color(31,111,235);
        Color botColor = new Color(40,60,100);

        JLabel header = new JLabel("🤖 Advanced Chatbot",JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(17,34,64));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial",Font.BOLD,18));
        header.setPreferredSize(new Dimension(100,50));
        frame.add(header,BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setBackground(bg);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        frame.add(scrollPane,BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(17,34,64));

        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");

        sendBtn.setBackground(userColor);
        sendBtn.setForeground(Color.WHITE);

        inputPanel.add(inputField,BorderLayout.CENTER);
        inputPanel.add(sendBtn,BorderLayout.EAST);

        frame.add(inputPanel,BorderLayout.SOUTH);

        addMessage("Bot","Hello! I'm your advanced AI 🤖",botColor,false);

        sendBtn.addActionListener(e->sendMessage(userColor,botColor));
        inputField.addActionListener(e->sendMessage(userColor,botColor));

        frame.setVisible(true);
    }

    static void sendMessage(Color userColor, Color botColor){

        String text = inputField.getText().trim();
        if(text.isEmpty()) return;

        messageCount++;

        addMessage("You",text,userColor,true);
        inputField.setText("");

        String response = getResponse(text);
        addMessage("Bot",response,botColor,false);

        saveChat(text,response);
    }

    static void addMessage(String sender,String message,Color color,boolean isUser){

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(10,25,47));

        JLabel msg = new JLabel("<html>" + message + "</html>");
        msg.setOpaque(true);
        msg.setBackground(color);
        msg.setForeground(Color.WHITE);
        msg.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JPanel bubble = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));
        bubble.setBackground(new Color(10,25,47));

        bubble.add(msg);
        wrapper.add(bubble,BorderLayout.CENTER);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(8));

        chatPanel.revalidate();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    //  AI UNDERSTANDING
    static String getResponse(String input){

        String text = input.toLowerCase();

        // Remove punctuation (basic NLP)
        text = text.replaceAll("[^a-zA-Z0-9 ]","");

        // keywords detection
        if(text.contains("hello") || text.contains("hi") || text.contains("salam")){
            return "Hello 😊";
        }

        if(text.contains("how are you")){
            return "I'm doing great! 💙";
        }

        if(text.contains("your name")){
            return "I'm your AI Chatbot 🤖";
        }

        if(text.contains("my name is")){
            String name = input.substring(input.toLowerCase().indexOf("my name is")+11).trim();
            memory.put("name",name);
            return "Nice to meet you " + name;
        }

        if(text.contains("who am i")){
            return memory.containsKey("name") ? "You are " + memory.get("name") : "I don't know yet";
        }

        if(text.contains("time")){
            return "Time: " + java.time.LocalTime.now().withNano(0);
        }

        if(text.contains("date")){
            return "Date: " + java.time.LocalDate.now();
        }

        if(text.contains("joke")){
            return "Programmers don’t like nature because of bugs 😄";
        }
        if(text.contains("what are you doing")){
            return "I am just here for you! You neend me, Just let me know 😄";
        }
        if(text.contains("I want to ask a question")){
            return "Yes please. I am here for you 😄";
        }
        if(text.contains("How are you")){
            return "I am great! what about you?";
        }
        if(text.contains("Can i ask you")){
            return "Yes Absolutely";
        }

        if(text.contains("study") || text.contains("exam")){
            return "Focus! 💪 you can do it!";
        }

         if(text.contains("need") || text.contains("want")){
            return "Yes sure, I will please to!";
        }

        if(text.contains("sad")){
            return "Don't worry 💙 I'm here for you";
        }

        if(text.contains("stock")){
            lastIntent="stock";
            return "Which stock?";
        }

        if(lastIntent.equals("stock")){
            lastIntent="";
            return "Nice 📈 good choice!";
        }

        if(text.contains("bye")){
            return "Goodbye 👋";
        }

        return "I understand a bit 😊 tell me more!";
    }

    static void saveChat(String user, String bot){
        try{
            FileWriter fw = new FileWriter("chat.txt",true);
            fw.write("User: "+user+"\nBot: "+bot+"\n\n");
            fw.close();
        }catch(Exception e){}
    }
}