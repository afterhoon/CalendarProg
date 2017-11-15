import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.border.*;

import java.io.*;

public class calendar extends JFrame{

	///////// TEST ///////
	JLabel testLa = new JLabel("(x,y)");
	/////////////////////

	Color color[] = { new Color(102, 0, 255), new Color(102, 51, 255), new Color(102, 102, 255)
			, new Color(102, 153, 255), new Color(102, 204, 255), new Color(102, 255, 255) };
	Color[] paint = new Color[30];
	Font font;
	
	JCalendar jcalendar;

	Vector<Schedule> vSd;
	JPanel contentPane;
	CalendarPanel calPan;
	AddPanel addPan;
	TotalInfoPanel tInfoPan;
	SubInfoPanel sInfoPan;
	TimeLabel timeLabel;
	Schedule targetSchedule = null;
	Calendar now = Calendar.getInstance();

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
	final int apdfx = 750;
	final int apdfy = 400;
	int apx = 750;
	int apy = 400;
	final int apWidth = 400;
	final int apHeight = 300;
	int apMoveWidth = 100;
	int apMoveHeight = 50;
	boolean bigMode = true; // addPan이 큰 상태면 true, 작은 상태면 false

	public calendar() {
		setTitle("내 마음속에 저장~♥");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		paintColor();
		initData();
		makeGUI();

		setResizable(false);
		setVisible(true);
		setSize(frameWidth, frameHeight);
	}

	private void initData() {
		jcalendar = new JCalendar();
		vSd = new Vector<Schedule>();
		loadDate();

		contentPane = new JPanel();
		calPan = new CalendarPanel();
		addPan = new AddPanel();
		tInfoPan = new TotalInfoPanel();
		sInfoPan = new SubInfoPanel();
		timeLabel = new TimeLabel();
	}

	private void paintColor() {
		paint[0]  = Color.WHITE;	// 전체 배경

		/* InfoPanel */
		paint[1]  = new Color(0,57,123);	// TotalInfoPanel 바탕
		paint[2]  = Color.WHITE;	// TotalInfoPanel 리스트
		paint[3]  = Color.DARK_GRAY;// SubInfoPanel 바탕
		paint[4]  = Color.LIGHT_GRAY;	// SubInfoPanel 리스트
		paint[5]  = Color.WHITE;	// CalendarPanel 바탕

		/* CalendarPanel */
		paint[18] = Color.BLACK;	// 연도 이동 버튼 글자 색
		paint[19] = Color.WHITE;	// 연도 이동 버튼 배경 색
		paint[20] = Color.BLACK;	// 달 이동 버튼 글자 색
		paint[21] = Color.WHITE;	// 달 이동 버튼 배경 색

		paint[6]  = Color.darkGray;	// 요일 글씨색
		paint[7]  = new Color(166,203,240);	// 요일 배경색
		paint[14] = Color.RED;		// 일요일 글자 색
		paint[15] = Color.BLUE;		// 토요일 글자 색

		paint[8]  = new Color(193,225,236);	// 날짜 아닌 곳 색

		paint[9]  = Color.WHITE;	// 일정 없는 날짜 색
		paint[10] = new Color(245,213,227);	// 일정 있는 날짜 색
		paint[11] =new Color(203,133,177);			// 중요인 날짜 색
		paint[12] = new Color(204,133,177);		// 생일 날짜 색
		paint[13] = new Color(144,111,168);		// 생일이고 중요한 날짜 색

		/* AddPanel */
		paint[16] = new Color(238,240,165);	// AddPanel 배경색
		paint[17] = Color.WHITE;	// AddPanel 내부 색

	}


	private void makeGUI() {
		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(null);

		contentPane.setOpaque(true);
		contentPane.setBackground(paint[0]);

		addComponent(contentPane, addPan, apx, apy, apWidth, apHeight);
		addComponent(contentPane, tInfoPan, ipx, ipy, ipWidth, ipHeight);
		addComponent(contentPane, sInfoPan, ipx, ipy, ipWidth, ipHeight);
		sInfoPan.setVisible(false);
		addComponent(contentPane, calPan, cpx, cpy, cpWidth, cpHeight);
		addComponent(contentPane, timeLabel, 550, 5, 200, 25);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				tInfoPan.setVisible(true);
				sInfoPan.setVisible(false);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			Point start;
			public void mouseMoved(MouseEvent e) {
				start = e.getPoint();
				testLa.setText("(" + start.x + "," + start.y + ")");
			}
			public void mouseDragged(MouseEvent e) {
				Point end = e.getPoint();
				testLa.setText("(" + start.x + "," + start.y + ")" + "  -->  " + "(" + end.x + "," + end.y + ")");
			}
		});

	}

	private class Schedule {
		private int year = 0;
		private int month = 0;
		private int day = 0;
		private int hour = 99;
		private int state = 1;	// 0: 기본  1: 일정있음  2: 중요  3: 생일  4: 중요와 생일
		private String content = "";

		Schedule() {

		}

		Schedule(int y, int m, int d, int h, String s, int st) {
			year = y;
			month = m;
			day = d;
			hour = h;
			content = s;
			state=st;
		}

		Schedule(int y, int m, int d) {
			year = y;
			month = m;
			day = d;
		}

		Schedule(Schedule sd) {
			year = sd.getYear();
			month = sd.getMonth();
			day = sd.getDay();
			hour = sd.getHour();
			state = sd.getState();
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

		public int getState() {
			return state;
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

		public void setState(int st){
			state=st;
		}

		public void setContent(String s) {
			content = s;
		}

		public String toString () {
			String str = year + "." + month + "." + day + "/ " + hour + "시/ " + content;
			return str;
		}

	}

	private class TotalInfoPanel extends JPanel {
		JList list = new JList(vSd);
		JPanel contentPane = new JPanel();

		public TotalInfoPanel() {
			setBorder(BorderFactory.createTitledBorder("Schedule"));
			setLayout(new BorderLayout());
			setContentPane(contentPane);
			setBackground(paint[1]);

			add(new JScrollPane(list));
			list.setBackground(paint[2]);

			list.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Schedule sc = (Schedule) list.getSelectedValue();
					if(list.getSelectedIndex() > -1){
						targetSchedule = sc;
						addPan.timeTf.setText(Integer.toString(sc.hour));
						addPan.contTa.setText(sc.content);
					}
				}
			});

			list.updateUI();
		}
	}

	private class SubInfoPanel extends JPanel {
		Vector<Schedule> vSubSd = new Vector<Schedule>();
		JList sList = new JList(vSubSd);
		JPanel contentPane = new JPanel();
		Schedule today = new Schedule(jcalendar.getYear(), jcalendar.getMonth(), jcalendar.getDay());

		public SubInfoPanel() {
			setVisible(false);
			setLayout(new BorderLayout());
			setContentPane(contentPane);
			setBackground(paint[3]);

			add(new JScrollPane(sList));
			sList.setBackground(paint[4]);

			todayVector();

			sList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Schedule sc = (Schedule) sList.getSelectedValue();
					if(sList.getSelectedIndex() > -1){
						targetSchedule = sc;
						addPan.timeTf.setText(Integer.toString(sc.hour));
						addPan.contTa.setText(sc.content);
					}
					sList.updateUI();
				}
			});

			sList.updateUI();
		}

		public void todayVector() {
			today.setYear(jcalendar.getYear());
			today.setMonth(jcalendar.getMonth());
			today.setDay(jcalendar.getDay());
			setBorder(BorderFactory.createTitledBorder(convertDate(today)/100 + " Schedule"));

			vSubSd.removeAllElements();
			for(int i = 0 ; i < vSd.size() ; i++) {
				if(convertDate(vSd.elementAt(i))/100 == convertDate(today)/100) {
					vSubSd.addElement(vSd.elementAt(i));
				}
			}
			sList.updateUI();
		}
	}

	private class CalendarPanel extends JPanel {
		JPanel frame;    //메인 프레임
		JPanel calendarPanel;  //달력 전체 패널
		JPanel changePanel;    //JSpinner, JComboBox 담고 있는 패널
		JPanel datePanel;    //날짜 부분 패널

		JButton lYearBut;
		JButton lMonBut;
		JButton nMonBut;
		JButton nYearBut;

		ArrayList<JButton> dateList;

		JLabel yearLa;
		JLabel monthLa;

		public CalendarPanel() {
			setLayout(new BorderLayout());


			frame = new JPanel();
			calendarPanel = new JPanel();
			changePanel = new JPanel();
			datePanel = new JPanel();

			calendarPanel.setOpaque(true);
			calendarPanel.setBackground(paint[5]);
			changePanel.setOpaque(true);
			changePanel.setBackground(paint[5]);
			datePanel.setOpaque(true);
			datePanel.setBackground(paint[5]);

			changePanel.setLayout(new GridLayout(1,6));

			dateList = new ArrayList<JButton>();

			calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));
			calendarPanel.setLayout(new BorderLayout());

			lYearBut = new JButton("<<");
			lYearBut.setForeground(paint[18]);
			changePanel.add(lYearBut).setBackground(paint[19]);
			lMonBut = new JButton("<");
			lMonBut.setForeground(paint[20]);
			changePanel.add(lMonBut).setBackground(paint[21]);

			yearLa = new JLabel(Integer.toString(jcalendar.getYear()));
			changePanel.add(yearLa);
			yearLa.setHorizontalAlignment(SwingConstants.CENTER);
			monthLa = new JLabel(Integer.toString(jcalendar.getMonth()));
			changePanel.add(monthLa);
			monthLa.setHorizontalAlignment(SwingConstants.CENTER);

			nMonBut = new JButton(">");
			nMonBut.setForeground(paint[20]);
			changePanel.add(nMonBut).setBackground(paint[21]);
			nYearBut = new JButton(">>");
			nYearBut.setForeground(paint[18]);
			changePanel.add(nYearBut).setBackground(paint[19]);

			lYearBut.addActionListener(new BtnListener());
			lMonBut.addActionListener(new BtnListener());
			nMonBut.addActionListener(new BtnListener());
			nYearBut.addActionListener(new BtnListener());

			changePanel.setPreferredSize(new Dimension(250, 40));
			calendarPanel.add(BorderLayout.NORTH, changePanel);


			datePanel.setLayout(new GridLayout(7, 7));  
			String[] dayOfWeekName = {"일", "월", "화", "수", "목", "금", "토"};  
			for (int i = 0; i < dayOfWeekName.length; i++) {
				datePanel.add(addJLabel(dayOfWeekName[i], paint[6], paint[7]));
			}

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				JButton button = addJButton("", paint[8]);
				dateList.add(button);
				datePanel.add(button);
				button.setEnabled(false);
			}

			font = (new JButton()).getFont();
			for (int i = 0; i < jcalendar.getLastday(); i++) {
				JButton button = null;
				switch(importance(new Schedule(jcalendar.getYear(), jcalendar.getMonth(), i + 1))) {
				case 0: button = addJButton(i + 1 + "", paint[9]); break;
				case 1: button = addJButton(i + 1 + "", paint[10]); break;
				case 2: button = addJButton(i + 1 + "", paint[11]); break;
				case 3: button = addJButton(i + 1 + "", paint[12]); break;
				case 4: button = addJButton(i + 1 + "", paint[13]); break;
				}
				if(convertDate(new Schedule(jcalendar.getYear(), jcalendar.getMonth(), i + 1))
						== convertDate(new Schedule(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DATE)))) {
					button.setFont(new Font(font.getFontName(),font.getStyle(), font.getSize() + 20));
				}
				else {
					button.setFont(new Font(font.getFontName(),font.getStyle(), font.getSize() + 10));
				}
				dateList.add(button);
				datePanel.add(button); 
				button.setEnabled(true);
			}

			int afterEmpty = 42 - jcalendar.getLastday() - jcalendar.getFirstdayOfWeek();
			for (int i = 0; i < afterEmpty; i++) {
				JButton button = addJButton("", paint[8]);
				dateList.add(button);
				datePanel.add(button);
				button.setEnabled(false);
			}

			setWeekend();

			calendarPanel.add(BorderLayout.CENTER, datePanel);

			add(BorderLayout.CENTER, calendarPanel);

		}

		public JButton addJButton(String text, Color color) {
			JButton button = new JButton();
			button.setText(text);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setBorder(new EtchedBorder());
			if (color != null) {
				button.setOpaque(true);
				button.setBackground(color);
			}
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JButton b = (JButton)e.getSource();
					String strDay = b.getText();
					if (strDay.indexOf("*") == 0)
						strDay = strDay.substring(1);

					jcalendar.setDay(Integer.parseInt(strDay));
					/*
					for(int j = 0 ; j < vSd.size() ; j++) {
						Schedule tempSd = new Schedule(jcalendar.getYear(), jcalendar.getMonth(), jcalendar.getDay());
						if(convertDate(tempSd)/100 == convertDate(vSd.elementAt(j))/100) {
							String str = vSd.elementAt(j).getHour() + "시/ " + vSd.elementAt(j).getContent();
							System.out.println(str);
						}
					}
					 * */
					sInfoPan.todayVector();
					tInfoPan.setVisible(false);
					sInfoPan.setVisible(true);
				}
			});

			return button;
		}

		public JLabel addJLabel(String text, Color fgColor, Color bgColor) {
			JLabel label = new JLabel();
			label.setText(text);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBorder(new EtchedBorder());
			if (color != null) {
				label.setOpaque(true);
				label.setBackground(bgColor);
				label.setForeground(fgColor);
			}
			return label;
		}

		private class BtnListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();

				int year = jcalendar.getYear();
				int month = jcalendar.getMonth();

				switch(b.getText()) {
				case "<<": year--; break;
				case "<": month--; break;
				case ">": month++; break;
				case ">>": year++; break;
				}

				if(month == 13) {
					month = 1;
					year++;
				}
				if(month == 0) {
					month = 12;
					year--;
				}

				jcalendar.setCalendar(year, month, 1);
				yearLa.setText(Integer.toString(jcalendar.getYear()));
				monthLa.setText(Integer.toString(jcalendar.getMonth()));

				resetDateList();
			}
		}

		public void resetDateList() {

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				dateList.get(i).setText("");
				dateList.get(i).setEnabled(false);
				dateList.get(i).setBackground(paint[8]);
			}
			for (int i = 0; i < jcalendar.getLastday(); i++) {
				dateList.get(jcalendar.getFirstdayOfWeek() + i).setText(i + 1 + "");
				dateList.get(jcalendar.getFirstdayOfWeek() + i).setEnabled(true);

				switch(importance(new Schedule(jcalendar.getYear(), jcalendar.getMonth(), jcalendar.getFirstdayOfWeek() + i - 2))) {
				case 0: dateList.get(jcalendar.getFirstdayOfWeek() + i).setBackground(paint[9]); break;
				case 1: dateList.get(jcalendar.getFirstdayOfWeek() + i).setBackground(paint[10]); break;
				case 2: dateList.get(jcalendar.getFirstdayOfWeek() + i).setBackground(paint[11]); break;
				case 3: dateList.get(jcalendar.getFirstdayOfWeek() + i).setBackground(paint[12]); break;
				case 4: dateList.get(jcalendar.getFirstdayOfWeek() + i).setBackground(paint[13]); break;
				}
				
				if(convertDate(new Schedule(jcalendar.getYear(), jcalendar.getMonth(), jcalendar.getFirstdayOfWeek() + i - 2))
						== convertDate(new Schedule(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DATE)))) {
					dateList.get(jcalendar.getFirstdayOfWeek() + i).setFont(new Font(font.getFontName(),font.getStyle(), font.getSize() + 20));
				}
				else {
					dateList.get(jcalendar.getFirstdayOfWeek() + i).setFont(new Font(font.getFontName(),font.getStyle(), font.getSize() + 10));
				}
			}
			int afterEmpty = jcalendar.getFirstdayOfWeek() + jcalendar.getLastday();
			int last = dateList.size() - afterEmpty;
			for (int i = 0; i < last; i++) {
				dateList.get(afterEmpty + i).setText("");
				dateList.get(afterEmpty + i).setEnabled(false);
				dateList.get(afterEmpty + i).setBackground(paint[8]);
			}

			setWeekend();
			datePanel.updateUI();
		}

		public void setWeekend() {
			// 요일에 알맞는 색을 칠해주는 메소드
			for (int i = 0; i < dateList.size(); i++) {

				if (i % 7 == 0) {
					dateList.get(i).setForeground(paint[14]);
				}
				else if (i % 7 == 6) {
					dateList.get(i).setForeground(paint[15]);
				}
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
		private int day;
		private int nowDay;

		public JCalendar() {

			cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			today = cal.get(Calendar.DAY_OF_MONTH);
			nowDay = cal.get(Calendar.DATE);
			setCalendar(year, month, 1);
		}

		public void setCalendar(int year, int month, int date) {

			cal.set(year, month - 1, date);
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

		public int getDay() {
			return day;
		}
		
		public int getNowDay() {
			return nowDay;
		}

		public void setDay(int d) {
			day = d;
		}

	}

	private class AddPanel extends JPanel {
		JLabel timeLa = new JLabel("시간");
		JLabel contLa = new JLabel("내용");
		JTextField timeTf = new JTextField(30);
		JTextArea contTa = new JTextArea(5, 30);

		int available = 0;
		int hour;
		String cont;

		private class Anim_Shrink extends Thread{
			public void run(){
				if(bigMode){
					int w = addPan.getWidth();
					int h = addPan.getHeight();

					for(int i = 0 ; i <= 101 ; i++) {
						try {
							sleep(3);
						} catch(InterruptedException e1) {

						}
						addPan.setSize(w - 3 * i, h - 2 * i);
						addPan.setLocation(new Point(addPan.getX() + i%2 + 1, addPan.getY() + 1));
						addPan.updateUI();
					}
					// 아래 두 문장 : 정확히 한 칸에 맞게 하기 위해
					addPan.setSize(addPan.getWidth(), addPan.getHeight() - 8);
					int x = ((addPan.getX() + 15) / 98) * 98 + 40 - 8;
					int y = ((addPan.getY() - 130) / 90 + 1) * 90 + 120 - 36;
					addPan.setLocation(new Point(x, y + 4));

					addPan.updateUI();
					try {
						sleep(100);
					} catch (InterruptedException e) {

					}
					apx = apdfx;
					apy = apdfy;
					addPan.setSize(apWidth, apHeight);
					addPan.setLocation(apx, apy);
					setTransparent(addPan, 255);

					timeLa.setVisible(true);
					contLa.setVisible(true);
					timeTf.setVisible(true);
					contTa.setVisible(true);

					bigMode = true;
				}
			}
		}

		private class Anim_Shift extends Thread{
			public void run(){
				if(bigMode){
					int oldX = addPan.getX();
					int oldY = addPan.getY();
					int newX = apdfx;
					int newY = apdfy;
					int w = addPan.getWidth();
					int h = addPan.getHeight();

					timeLa.setVisible(false);
					contLa.setVisible(false);
					timeTf.setVisible(false);
					contTa.setVisible(false);

					for(int i = 0 ; i <= 101 ; i++) {
						try {
							sleep(3);
						} catch(InterruptedException e1) {

						}
						addPan.setLocation(new Point((int)(oldX + (newX-oldX)/101.0 * i), (int)(oldY + (newY-oldY)/101.0 * i)));
						addPan.updateUI();
					}

					apx = apdfx;
					apy = apdfy;
					addPan.setSize(apWidth, apHeight);
					addPan.setLocation(apx, apy);
					setTransparent(addPan, 255);

					timeLa.setVisible(true);
					contLa.setVisible(true);
					timeTf.setVisible(true);
					contTa.setVisible(true);

					bigMode = true;
				}
			}
		}

		private class Anim_Expand extends Thread{
			public void run(){
				if(!bigMode){
					int w = addPan.getWidth();
					int h = addPan.getHeight();

					for(int i = 0 ; i <= 101 ; i++) {
						try {
							sleep(5);
						} catch(InterruptedException e1) {

						}
						addPan.setSize(w + 3 * i, h + 2 * i);
						addPan.setLocation(new Point(addPan.getX() - i%2 - 1, addPan.getY() - 1));
						addPan.updateUI();
					}
					addPan.setSize(addPan.getWidth(), addPan.getHeight() + 8); // 사이즈 원상복구

					addPan.updateUI();
					bigMode = true;
				}

				addPan.setLocation(apdfx, apdfy);
			}
		}

		private AddPanel() {
			Schedule temp = new Schedule();
			setBorder(BorderFactory.createTitledBorder("Memo"));
			setOpaque(true);
			setBackground(paint[16]);

			setLayout(new FlowLayout());

			add(timeLa).setBackground(paint[17]);
			add(timeTf).setBackground(paint[17]);
			add(contLa).setBackground(paint[17]);
			add(contTa).setBackground(paint[17]);

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						hour = Integer.parseInt(timeTf.getText());
					} catch(NumberFormatException e1) {
						hour = -1;
					}
					cont = contTa.getText();

					int st=findDayState(cont);
					temp.setState(st);

					// 투명도 설정
					setTransparent(addPan, 100);

					if(hour >= 0 && hour <= 24)
						temp.setHour(hour);
					else temp.setHour(99);
					temp.setContent(cont);

					if(temp.getHour() == 99) {
						available = 0;
					}
					else {
						available = 1;
					}

					timeLa.setVisible(false);
					contLa.setVisible(false);
					timeTf.setVisible(false);
					contTa.setVisible(false);
					if(targetSchedule != null) System.out.println("targetSchedule: " + targetSchedule.toString());
					else System.out.println("targetSchedule: x");
				}

				public void mouseReleased(MouseEvent e) {
					int temX = apx+addPan.getWidth()/2;
					int temY = apy+addPan.getHeight()/2;

					if(available == 0) {
						// 제자리로 돌아가는 애니메이션
						new Anim_Shift().start(); 

						// 투명도 설정 (불투명하게)
						setTransparent(addPan, 255);
					}
					else if((temX >= cpx && temX <= cpx + cpWidth) && (temY >= cpy && temY <= cpy + cpHeight)) {
						temp.setYear(jcalendar.getYear());
						temp.setMonth(jcalendar.getMonth());
						temp.setDay(searchDay(temX, temY));
						if(searchDay(temX, temY) == 0) {
							// 제자리로 돌아가는 애니메이션
							new Anim_Shift().start(); 
							// 투명도 설정 (불투명하게)
							setTransparent(addPan, 255);
							return;
						}

						if(targetSchedule == null) {
							insertSchedule(new Schedule(temp));
						}
						else {
							shiftSchedule(searchDay(temX, temY));
						}

						tInfoPan.list.updateUI();
						sInfoPan.sList.updateUI();

						timeTf.setText("");
						contTa.setText("");

						// 사이즈 작아지는 애니메이션
						new Anim_Shrink().start(); 

						setTransparent(addPan, 100);

					}
					else if (temX >= frameWidth || temY >= frameHeight) {
						apx = apdfx;
						apy = apdfy;
						addPan.setLocation(apx, apy);

						deleteSchedule();

						timeLa.setVisible(true);
						contLa.setVisible(true);
						timeTf.setVisible(true);
						contTa.setVisible(true);
						setTransparent(addPan, 255);
					}
					else if ((temX >= ipx && temX <= ipx + ipWidth) && (temY >= ipy && temY <= ipy + ipHeight)) {
						apx = apdfx;
						apy = apdfy;
						addPan.setLocation(apx, apy);

						modifySchedule();

						timeLa.setVisible(true);
						contLa.setVisible(true);
						timeTf.setVisible(true);
						contTa.setVisible(true);
						setTransparent(addPan, 255);
					}
					else {
						// 제자리로 돌아가는 애니메이션
						new Anim_Shift().start(); 

						// 투명도 설정 (불투명하게)
						setTransparent(addPan, 255);
					}
					addPan.updateUI();
					calPan.resetDateList();

					saveDate();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int tem_x = e.getX()-(addPan.getWidth()/2); // 이벤트 발생 좌표값중 x값 추출
					int tem_y = e.getY()-(addPan.getHeight()/2); // 이벤트 발생 좌표값중 y값 추출..

					apx = apx + tem_x;
					apy = apy + tem_y;

					addPan.setBounds(apx, apy, addPan.getWidth(), addPan.getHeight());
				}  
			});

		}

		private int searchDay(int x, int y) {
			int day = 0;
			int calX, calY;
			int firstDay = jcalendar.getFirstdayOfWeek();
			int lastDay = jcalendar.getLastday();

			calX = (x - 40) / 98 + 1;
			calY = (y - 90) / 90 - 1;

			if ((calX >= 0 && calX <= 7) && (calY >= 0 && calY <= 5))
				day = calY * 7 + calX - firstDay;
			else day = 0;

			if (day < 1 || day > lastDay)
				day = 0;

			return day;
		}
	}

	// 투명도 설정 -> 반투명하게 하면 가끔씩 글씨체에 문제가 생김
	public void setTransparent(JPanel pan, int percent) {
		pan.setBackground(new Color(
				pan.getBackground().getRed(), 
				pan.getBackground().getGreen(), 
				pan.getBackground().getBlue(),
				percent));
	}

	private int findDayState(String st){
		if(st.matches(".*생일.*") && st.matches(".*중요.*")){
			return 4;
		}else if(st.matches(".*생일.*")){
			return 3;
		}else if(st.matches(".*중요.*")){
			return 2;
		}else return 1;
	}

	public void deleteSchedule() {
		if(targetSchedule == null)
			return;

		//		if(isEmpty(convertDate(targetSchedule)))
		//			calPan.dateList.get(targetSchedule.getDay() + calPan.jcalendar.getFirstdayOfWeek() - 1).setText("*" + targetSchedule.getDay());
		//		else
		//			calPan.dateList.get(targetSchedule.getDay() + calPan.jcalendar.getFirstdayOfWeek() - 1).setText("" + targetSchedule.getDay());

		vSd.remove(targetSchedule);
		sInfoPan.todayVector();

		sInfoPan.sList.updateUI();
		tInfoPan.list.updateUI();
		addPan.timeTf.setText("");
		addPan.contTa.setText("");
		targetSchedule = null;
	}


	public void modifySchedule() {
		if(targetSchedule == null)
			return;

		targetSchedule.setHour(Integer.parseInt(addPan.timeTf.getText()));
		targetSchedule.setContent(addPan.contTa.getText());

		insertSchedule(new Schedule(targetSchedule));
		deleteSchedule();

		sInfoPan.todayVector();
		tInfoPan.list.updateUI();
		sInfoPan.sList.updateUI();
		addPan.timeTf.setText("");
		addPan.contTa.setText("");
		targetSchedule = null;
	}

	public void shiftSchedule(int day) {
		if(targetSchedule == null)
			return;

		targetSchedule.setYear(jcalendar.getYear());
		targetSchedule.setMonth(jcalendar.getMonth());
		targetSchedule.setDay(day);

		insertSchedule(new Schedule(targetSchedule));
		deleteSchedule();

	}

	private void insertSchedule(Schedule sd) {
		System.out.println("insert: " + sd.toString());
		if (vSd.size() != 0) {
			for(int i = 0 ; i < vSd.size() ; i++) {
				if(convertDate(sd) < convertDate(vSd.elementAt(i))) {
					vSd.add(i, new Schedule(sd));
					return;	
				}
			}
		}
		vSd.addElement(new Schedule(sd));
	}

	public int isEmpty(int date) {
		if(vSd.size() > 0) {
			for(int i = 0 ; i < vSd.size(); i++) {
				if(convertDate(vSd.elementAt(i))/100 == date/100) {
					//					System.out.println("대상 date: " + date + "//////-> " + convertDate(vSd.elementAt(i))/100 + " / " + date/100);
					return 1;
				}
			}
		}
		return 0;
	}

	public int importance(Schedule sd) {
		int res = 0;
		int date = convertDate(sd);
		for(int i = 0 ; i < vSd.size(); i++) {
			if(convertDate(vSd.elementAt(i))/100 == date/100) {
				if(res < vSd.elementAt(i).getState())
					res = vSd.elementAt(i).getState();
				else if(res == 2 && vSd.elementAt(i).getState() == 3)
					res = 4;
				else if(res == 3 && vSd.elementAt(i).getState() == 2)
					res = 4;
			}
		}
		//		System.out.println(day + "의 state: " + res);
		return res;
	}

	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	private int convertDate(Schedule sd) {
		int date = 0;
		date += sd.getYear() * 1000000;
		date += sd.getMonth() * 10000;
		date += sd.getDay() * 100;
		date += sd.getHour();
		return date;
	}

	public void loadDate() {
		try {
			FileReader in = new FileReader("date.txt");
			int c;
			int cnt = 0;
			int y, m, d, h;
			String date = new String();
			String cont = new String();
			Schedule temp;
			while((c = in.read()) != -1) {
				if(cnt < 10) {
					date = date + (char)c;
					cnt++;
				}
				else {
					if(c == '/') {
						temp = new Schedule();
						int dt = Integer.parseInt(date);
						h = dt % 100;
						d = (dt % 10000 - h) / 100;
						m = (dt % 1000000 - h - d) / 10000;
						y = dt / 1000000;
						temp.setYear(y);
						temp.setMonth(m);
						temp.setDay(d);
						temp.setHour(h);
						temp.setContent(cont);
						temp.setState(findDayState(cont));
						System.out.println(temp.getDay() + "의 state: " + temp.getState());
						vSd.addElement(temp);

						cnt = 0;
						date = "";
						cont = "";
						continue;
					}
					cont = cont + (char)c;
				}
			}

			in.close();
		} catch(Exception e) { }

	}

	public void saveDate() {
		try {
			FileWriter out = new FileWriter("date.txt");
			for(int i = 0 ; i < vSd.size(); i++) {
				String str = convertDate(vSd.elementAt(i)) + vSd.elementAt(i).getContent() + "/";
				out.write(str);
			}
			out.close();
		} catch(Exception e) { }

	}


	private class TimeLabel extends JLabel {
		Calendar now;
		int y, m, d, ap, h, min, s;
		int cnt = 0;
		class TimeRefresh extends Thread{
			public void run(){
				String time; 
				while(true){ 
					time = ""; 
					time += y;
					time += ".";
					time += m;
					time += ".";
					time += d;
					time += " / ";
					time += (h < 10) ? "0" + h : h; 
					time += ":"; 
					time += (min < 10) ? "0" + min : min; 
					time += ":"; 
					time += (s < 10) ? "0" + s : s; 
					setText(time); 
					s++; 
					
					if (s == 60) { min++; s = 0; }
					if (min == 60) { h++; min = 0; }
					if (h == 24) { d++; h = 0; }
					if (d == 31) { m++; d = 1; }
					if (m == 12) { y++; m = 1; }
					
					try{ 
						sleep(1000); 
					} catch(InterruptedException e){ 
						return; 
					} 
				} 
			} 
		}

		TimeLabel() {
			Calendar now = Calendar.getInstance();
			y = now.get(Calendar.YEAR);
			m = now.get(Calendar.MONTH) + 1;
			d = now.get(Calendar.DATE);
			ap = now.get(Calendar.AM_PM);
			h = now.get(Calendar.HOUR) + (ap == 0 ? 0 : 12);
			min = now.get(Calendar.MINUTE);
			s = now.get(Calendar.SECOND);
			setText(y + "." + m + "." + d + " / " + h + ":" + min + ":" + s);
			new TimeRefresh().start();
		}
	}

	public static void main(String[] args) {
		new calendar();
	}
}
