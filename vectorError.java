/*
 * Vector<Schedule> 부분에 문제가 있음
 * 
 * 참고 라인 (InfoPanel 클래스 내부)
 * JList list = new JList(vSd); // Schedule클래스 벡터인 vSd가 list에 정상적으로 들어가지 않음
 * vSd.addElement(new Schedule(2017, 11 , 5, 10, "윈프 과제")); // vSd에 addElement 시도시 NullPointerException 발생
 * 
 * */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.border.*;

public class calendar extends JFrame{
	JPanel contentPane = new JPanel();
	InfoPanel infoPan = new InfoPanel();
	Vector<Schedule> vSd = new Vector<Schedule>(100);
	Schedule targetSchedule = null;

	final int frameWidth = 1200;
	final int frameHeight = 800;

	// InfoPanel의 (x, y, width, height)
	int ipx = 750;
	int ipy = 35;
	final int ipWidth = 400;
	final int ipHeight = 300;


	public calendar() {
		setTitle("Calendar Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		makeGUI();

		setVisible(true);
		setSize(frameWidth, frameHeight);
	}

	private void makeGUI() {
		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(null);

		addComponent(contentPane, infoPan, ipx, ipy, ipWidth, ipHeight);
	}
	
	private class InfoPanel extends JPanel {
		JList list = new JList(vSd); // Schedule클래스 벡터인 vSd가 list에 정상적으로 들어가지 않음
		JPanel contentPane = new JPanel();
		public InfoPanel() {
			setBorder(BorderFactory.createTitledBorder("Schedule"));
			setLayout(new BorderLayout());
			setContentPane(contentPane);
			setBackground(new Color(95, 216, 250));

			add(new ScrollPane().add(list)).setBackground(new Color(151, 234, 244));
			vSd.addElement(new Schedule(2017, 11 , 5, 10, "윈프 과제")); // vSd에 addElement 시도시 NullPointerException 발생

			list.updateUI();
		}

	}

	private class Schedule {
		private int year = 0;
		private int month = 0;
		private int day = 0;
		private int hour = 0;
		private String content = "";

		Schedule() {

		}

		Schedule(int y, int m, int d, int h, String s) {
			year = y;
			month = m;
			day = d;
			hour = h;
			content = s;
		}

		Schedule(Schedule sd) {
			year = sd.getYear();
			month = sd.getMonth();
			day = sd.getDay();
			hour = sd.getHour();
			content = sd.getContent();
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDay() {
			return day;
		}

		public int getHour() {
			return hour;
		}

		public String getContent() {
			return content;
		}

		public void setYear(int y) {
			year = y;
		}

		public void setMonth(int m) {
			month = m;
		}

		public void setDay(int d) {
			day = d;
		}

		public void setHour(int h) {
			hour = h;
		}

		public void setContent(String s) {
			content = s;
		}
		public String toString () {
			String str = year + "." + month + "." + day + "/ " + hour + "시/ " + content;
			return str;
		}

	}
	
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	public static void main(String[] args) {
		new calendar();
	}
}
