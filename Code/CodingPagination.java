import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Frankline Sable on 01/08/2017.
 */
public class CodingPagination{
private JFrame frame;
private JPanel panel;
private JLabel label;
private  String arr[]={"<<","1","2","3",">>"};
private  JButton buttons[]=new JButton[5];
    public CodingPagination(){
        frame=new JFrame("Testing Pagination");
        frame.setSize(500,350);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        panel=new JPanel();
        panel.setBackground(Color.CYAN);
        panel.setBounds(20,5,450,300);
        panel.setLayout(null);

        label=new JLabel("Default");
        label.setBounds(150,200,300,50);
        int x=15;
        for(int i=0;i<(arr.length);i++){
            buttons[i]=new JButton(arr[i]);
            buttons[i].addActionListener(new ActionHandler());
            buttons[i].setBounds(x ,100,64,48);
            panel.add(buttons[i]);
            x+=70;
        }
        panel.add(label);
        frame.add(panel);
    }
    public class ActionHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==buttons[0]){
                nextClick(-1);
            }else if(e.getSource()==buttons[arr.length-1]){
                nextClick(+1);
            }else{
                bClick(e.getActionCommand());
            }

        }
    }
    int currentPage=1;
    int availablePages=arr.length;
    public void nextClick(int val){
        currentPage=currentPage+val;
        if(currentPage>availablePages){
            currentPage=1;
        }else if(currentPage<0){
            currentPage=availablePages;
        }
        updateLab(currentPage);
    }
    public void  bClick(String s){
        currentPage=Integer.parseInt(s);
        updateLab(currentPage);
    }
    public void updateLab(int vPage){
        label.setText(""+vPage);
    }
    public static void main(String[] args){
        new CodingPagination();
    }
}

