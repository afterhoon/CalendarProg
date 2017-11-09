package calender;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.border.*;

public class calendar extends JFrame{
	JPanel contentPane = new JPanel();
	CalendarPanel calPan = new CalendarPanel();
	AddPanel addPan = new AddPanel();
	InfoPanel infoPan = new InfoPanel();

	final int frameWidth = 1200;
	final int frameHeight = 800;

	// InfoPanel의 (x, y, width, height)
	int ipx = 750;
	int ipy = 35;
	final int ipWidth = 400;
	final int ipHeight = 300;

	// CalendarPanel의 (x, y, width, height)
	int cpx = 25;
	int cpy = 25;
	final int cpWidth = 700;
	final int cpHeight = 700;

	// 각각 AddPanel의 (x, y, width, height)
	int apx = 750;
	int apy = 400;
	final int apWidth = 400;
	final int apHeight = 300;
	int apMoveWidth = 100;
	int apMoveHeight = 50;

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

		addPan.addMouseListener(new AddPaneAdapter() );
		addPan.addMouseMotionListener(new AddPaneMove() );

		addComponent(contentPane, addPan, apx, apy, apWidth, apHeight);
		addComponent(contentPane, infoPan, ipx, ipy, ipWidth, ipHeight);
		addComponent(contentPane, calPan, cpx, cpy, cpWidth, cpHeight);
	}

	private class InfoPanel extends JPanel {
		String[] schedule = {"윈프 과제", "공부하기", "비엔날레 가기"};
		JList list = new JList(schedule);
		JPanel contentPane = new JPanel();
		public InfoPanel() {
			setBorder(BorderFactory.createTitledBorder("Schedule"));
			setLayout(new BorderLayout());
			setContentPane(contentPane);
			add(list);
			list.setOpaque(false);

//			addComponent(contentPane, list, 1100, ipHeight/2 - 10/2,100,10);
		}
	}

	private class CalendarPanel extends JPanel implements ActionListener, ChangeListener {

		JPanel frame;     //메인 프레임
		JPanel calendarPanel;   //달력 전체 패널
		JPanel changePanel;    //JSpinner, JComboBox 담고 있는 패널
		JPanel datePanel;    //날짜 부분 패널

		JButton lYearBut;
		JButton lMonBut;
		JButton nMonBut;
		JButton nYearBut;

		JCalendar jcalendar;   //JCalendar 객체
		ArrayList<JLabel> dateList;  //날짜JLabel를 담을 리스트

		JSpinner changeYear;
		JComboBox changeMonth;

		public CalendarPanel() {
			setOpaque(true);
			setBackground(Color.BLUE);
			setBounds(300, 400, 500, 700);
			setLayout(new BorderLayout());


			frame = new JPanel();
			calendarPanel = new JPanel();
			changePanel = new JPanel();
			datePanel = new JPanel();

			jcalendar = new JCalendar();
			dateList = new ArrayList<JLabel>();

			calendarPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Java Calendar"));
			calendarPanel.setLayout(new BorderLayout());

			SpinnerModel yearModel = new SpinnerNumberModel(jcalendar.getYear(), jcalendar.getYear()-100, jcalendar.getYear()+100, 1);
			changeYear = new JSpinner(yearModel);
			changeYear.setEditor(new JSpinner.NumberEditor(changeYear, "#"));  

			String[] month = new String[12];
			for (int i = 0; i < month.length; i++) {
				month[i] = i + 1 + "월";
			}
			changeMonth = new JComboBox(month);
			changeMonth.setSelectedIndex(jcalendar.getMonth());

			changeYear.addChangeListener(this);
			changeMonth.addActionListener(this);

			changeMonth.setPreferredSize(new Dimension(60, 22));
			changePanel.add(changeYear);
			changePanel.add(changeMonth);

			changePanel.setPreferredSize(new Dimension(250, 40));
			calendarPanel.add(BorderLayout.NORTH, changePanel);


			datePanel.setLayout(new GridLayout(7, 7));  
			String[] dayOfWeekName = {"일", "월", "화", "수", "목", "금", "토"};  
			for (int i = 0; i < dayOfWeekName.length; i++) {
				datePanel.add(addJLabel(dayOfWeekName[i], Color.WHITE));
			}  

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				JLabel label = addJLabel("", null);
				dateList.add(label);
				datePanel.add(label);
			}

			for (int i = 0; i < jcalendar.getLastday(); i++) {
				JLabel label = addJLabel(i + 1 + "", null);
				dateList.add(label);
				datePanel.add(label); 
			}

			int afterEmpty = 42 - jcalendar.getLastday() - jcalendar.getFirstdayOfWeek();
			for (int i = 0; i < afterEmpty; i++) {
				JLabel label = addJLabel("", null);
				dateList.add(label);
				datePanel.add(label);
			}

			setWeekend();

			calendarPanel.add(BorderLayout.CENTER, datePanel);

			add(BorderLayout.CENTER, calendarPanel);

		}

		public JLabel addJLabel(String text, Color color) {
			JLabel label = new JLabel();
			label.setText(text);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBorder(new EtchedBorder());
			if (color != null) {
				label.setOpaque(true);
				label.setBackground(color);
			}
			return label;
		}

		public void actionPerformed(ActionEvent e) {

			JComboBox obj = (JComboBox)e.getSource();
			int month = obj.getSelectedIndex();
			jcalendar.setCalendar(jcalendar.getYear(), month, 1);
			resetDateList();
		}

		public void stateChanged(ChangeEvent e) {

			JSpinner obj = (JSpinner)e.getSource();
			Integer y = (Integer)obj.getValue();
			int year = y.intValue();
			jcalendar.setCalendar(year, jcalendar.getMonth(), 1);
			resetDateList();
		}

		public void resetDateList() {

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				dateList.get(i).setText("");
			}
			for (int i = 0; i < jcalendar.getLastday(); i++) {
				dateList.get(jcalendar.getFirstdayOfWeek() + i).setText(i + 1 + "");
			}
			int afterEmpty = jcalendar.getFirstdayOfWeek() + jcalendar.getLastday();
			int last = dateList.size() - afterEmpty;
			for (int i = 0; i < last; i++) {
				dateList.get(afterEmpty + i).setText("");
			}

			setWeekend();
			datePanel.updateUI();
		}

		public void setWeekend() {

			for (int i = 0; i < dateList.size(); i++) {

				if (i % 7 == 0) {
					dateList.get(i).setForeground(Color.RED);
				}
				else if (i % 7 == 6) {
					dateList.get(i).setForeground(Color.blue);
				}
			}

		}

		public class JCalendar {

			private Calendar cal;
			private int year;    //연
			private int month;    //월
			private int today;    //오늘
			private int firstdayOfWeek;  //1일의 요일
			private int lastday;   //한달의 최대 날짜

			public JCalendar() {

				cal = Calendar.getInstance();
				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH);
				today = cal.get(Calendar.DAY_OF_MONTH);

				setCalendar(year, month, 1);
			}

			public void setCalendar(int year, int month, int date) {

				cal.set(year, month, date);
				this.year = year;
				this.month = month;
				firstdayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
				lastday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			}

			public void setFirstdayOfWeek(int firstdayOfWeek) {
				this.firstdayOfWeek = firstdayOfWeek;
			}

			public Calendar getCal() {
				return cal;
			}

			public int getFirstdayOfWeek() {
				return firstdayOfWeek;
			}

			public int getLastday() {
				return lastday;
			}

			public int getMonth() {
				return month;
			}

			public int getToday() {
				return today;
			}

			public int getYear() {
				return year;
			}

		}
	}


	private class AddPanel extends JPanel {
		private AddPanel() {
			setBorder(BorderFactory.createTitledBorder("Memo"));
			setOpaque(true);
			setBackground(Color.RED);
		}
	}

	private class AddPaneAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			System.out.println("Pressed!!");
		}

		public void mouseReleased(MouseEvent e) {
			int tem_x = e.getX()-(apWidth/2); // 이벤트 발생 좌표값중 x값 추출
			int tem_y = e.getY()-(apHeight/2); // 이벤트 발생 좌표값중 y값 추출..

			apx = apx + tem_x;
			apy = apy + tem_y;

			addPan.setBounds(apx, apy, apWidth, apHeight);
		}
	}

	private class AddPaneMove extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent e) {
			int tem_x = e.getX()-(apWidth/2); // 이벤트 발생 좌표값중 x값 추출
			int tem_y = e.getY()-(apHeight/2); // 이벤트 발생 좌표값중 y값 추출..

			apx = apx + tem_x;
			apy = apy + tem_y;

			addPan.setBounds(apx, apy, apWidth, apHeight);
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
