package tsp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class CSC_1D extends JFrame {


    private static final long serialVersionUID = 1L;
    public static int START_X = 300;
    public static int START_Y = 80;
    public static int WIDTH = 1400;
    public static int HEIGHT = 600;
    public static int left_base = 100;
    public static int down_base = HEIGHT * 3 / 4;


    public static boolean complete = false;
    public static int MAX = 100;
    public static CSC_1D gui;
    public static int min = Integer.MAX_VALUE;
    static int size = 9;
    static int[] a = new int[size];
    public static int max = a[size - 1] - a[0];
    static int SEG = 3;
    static int[] starts = new int[size];

    public CSC_1D() {

        super();
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocation(START_X, START_Y);
        this.setTitle("CSC-1D");
        this.setLayout(new BorderLayout());
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
        this.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());


        JButton button = new JButton("执行算法");
        button.setLocation(0, 0);
        JButton b = new JButton("重新生成");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //gui.setVisible(false);
                gui.dispose();
                gui = new CSC_1D();
                complete = false;
                randomrize();
            }
        });

        panel.add(b);
        panel.add(button);

        this.add(panel, BorderLayout.SOUTH);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 0; i < size - 1; i++) {
                    if (a[i + 1] - a[i] < min) {
                        min = a[i + 1] - a[i];
                        //System.out.println("here");
                    }

                }

                //System.out.println(min+" "+max);
                System.out.println(get_max(min, max));
                complete = true;
                gui.repaint();

            }
        });
    }

    public static void main(String args[]) {

        gui = new CSC_1D();
        randomrize();

		/*
        for(int i=0;i<SEG-1;i++) {
			for(int j=starts[i];j<=starts[i+1];j++) {
				System.out.print(a[j]+" ");
			}
			System.out.print("\t");
		}


		for(int j=starts[SEG-1]+1;j<size;j++) {
			System.out.print(a[j]+" ");
		}
			*/
    }

    public static void randomrize() {

        Random r = new Random();
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            int temp = r.nextInt(MAX - 10);
            if (!list.contains(temp)) ;
            list.add(temp);

        }

        Collections.sort(list);

        for (int i = 0; i < size; i++) {
            a[i] = list.get(i);

        }

        max = a[size - 1] - a[0];

    }

    public static boolean judge(int mid) {

        int seg_num = 0;
        int distance = 0;
        int start = 0;

        for (int i = 0; i < SEG; i++) {
            starts[i] = 0;
        }

        for (int i = 0; i < size; i++) {
            distance = a[i] - a[start];
            if (distance > mid) {

                start = i;
                seg_num++;
                starts[seg_num] = i;

            }
        }

        if (seg_num >= SEG)
            return false;
        else
            return true;
    }

    public static int get_max(int low, int high) {
        if (low > high)
            return high + 1;
        else {

            int mid = (low + high) / 2;
            if (judge(mid)) {


                return get_max(low, mid - 1);
            } else {

                return get_max(mid + 1, high);
            }
        }


    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(left_base, down_base, WIDTH * 3 / 4, 10);
        drawPoints(g);

    }

    public void drawPoints(Graphics g) {
        Color c = g.getColor();

        if (complete) {
            g.setColor(Color.RED);
            for (int i = 0; i < SEG - 1; i++) {
                int x = left_base + a[starts[i]] * (WIDTH * 3 / 4) / (MAX + 10);
                int x_next = left_base + a[starts[i + 1] - 1] * (WIDTH * 3 / 4) / (MAX + 10);
                g.fillRect(x + 11 / 2, down_base, x_next - x, 10);

            }

            int x = left_base + a[starts[SEG - 1]] * (WIDTH * 3 / 4) / (MAX + 10);
            int x_next = left_base + a[size - 1] * (WIDTH * 3 / 4) / (MAX + 10);
            g.fillRect(x + 11 / 2, down_base, x_next - x, 10);

        }


        g.setColor(Color.BLACK);
        int top_down = -1;
        for (int i = 0; i < size; i++) {
            int x = left_base + a[i] * (WIDTH * 3 / 4) / (MAX + 10);
            g.fillOval(x, down_base, 11, 11);
            g.drawLine(x + 11 / 2, down_base - 40, x + 11 / 2, down_base + 40);
            g.drawString(a[i] + "", x + 11 / 2, down_base + 50 * top_down);
            top_down *= (-1);
        }

        g.setColor(c);
    }

}
