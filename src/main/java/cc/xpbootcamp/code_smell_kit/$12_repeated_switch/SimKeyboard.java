package cc.xpbootcamp.code_smell_kit.$12_repeated_switch;


import java.awt.*;
import java.awt.event.*;

class SimKeyboard extends Panel {

	SimKeyboard(SimDisplay display,
				SimEnvelopeAcceptor envelopeAcceptor) {
		this.display = display;
		this.envelopeAcceptor = envelopeAcceptor;

		setLayout(new GridLayout(5, 3));

		Button[] digitKey = new Button[10];
		for (int i = 1; i < 10; i++) {
			digitKey[i] = new Button("" + i);
			add(digitKey[i]);
		}

		add(new Label(""));

		digitKey[0] = new Button("0");
		add(digitKey[0]);

		add(new Label(""));


		Button enterKey = new Button("ENTER");
		enterKey.setForeground(Color.black);
		enterKey.setBackground(new Color(128, 128, 255)); // Light blue
		add(enterKey);

		Button clearKey = new Button("CLEAR");
		clearKey.setForeground(Color.black);
		clearKey.setBackground(new Color(255, 128, 128)); // Light red
		add(clearKey);

		Button cancelKey = new Button("CANCEL");
		cancelKey.setBackground(Color.red);
		cancelKey.setForeground(Color.black);
		add(cancelKey);


		for (int i = 0; i < 10; i++)
			digitKey[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					digitKeyPressed(Integer.parseInt(e.getActionCommand()));
				}
			});

		enterKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterKeyPressed();
			}
		});

		clearKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearKeyPressed();
			}
		});

		cancelKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelKeyPressed();
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				char keyChar = e.getKeyChar();
				int keyCode = e.getKeyCode();
				if (keyChar >= '0' && keyChar <= '9') {
					digitKeyPressed(keyChar - '0');
					e.consume();
				} else {
					switch (keyCode) {
						case KeyEvent.VK_ENTER:

							enterKeyPressed();
							break;

						case KeyEvent.VK_CLEAR:

							clearKeyPressed();
							break;

						case KeyEvent.VK_CANCEL:
						case KeyEvent.VK_ESCAPE:

							cancelKeyPressed();
							break;
					}
					e.consume();
				}
			}
		});

		currentInput = new StringBuffer();
		mode = IDLE_MODE;
	}


	synchronized String readInput(int mode, int maxValue) {
		this.mode = mode;
		this.maxValue = maxValue;
		currentInput.setLength(0);
		cancelled = false;
		if (mode == AMOUNT_MODE)
			setEcho("0.00");
		else
			setEcho("");
		requestFocus();

		try {
			wait();
		} catch (InterruptedException e) {
		}

		this.mode = IDLE_MODE;

		if (cancelled)
			return null;
		else
			return currentInput.toString();
	}
////repeated switch

	private synchronized void digitKeyPressed(int digit) {
		switch (mode) {
			case IDLE_MODE:
				break;
			case PIN_MODE: {
				currentInput.append(digit);
				StringBuffer echoString = new StringBuffer();
				for (int i = 0; i < currentInput.length(); i++)
					echoString.append('*');
				setEcho(echoString.toString());
				break;
			}

			case AMOUNT_MODE: {
				currentInput.append(digit);
				String input = currentInput.toString();
				if (input.length() == 1)
					setEcho("0.0" + input);
				else if (input.length() == 2)
					setEcho("0." + input);
				else
					setEcho(input.substring(0, input.length() - 2) + "." +
							input.substring(input.length() - 2));
				break;
			}

			case MENU_MODE: {
				if (digit > 0 && digit <= maxValue) {
					currentInput.append(digit);
					notify();
				} else
					getToolkit().beep();
				break;
			}
			default:
				throw new IllegalStateException("Unexpected value: " + mode);
		}
	}

	/**
	 * Handle the ENTER key
	 */
	private synchronized void enterKeyPressed() {
		switch (mode) {
			case IDLE_MODE:
				break;
			case PIN_MODE:
			case AMOUNT_MODE:{
				if (currentInput.length() > 0)
					notify();
				else
					getToolkit().beep();
				break;}
			case MENU_MODE:
				getToolkit().beep();
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + mode);
		}
	}

	/**
	 * Handle the CLEAR key
	 */
	private synchronized void clearKeyPressed() {
		switch (mode) {
			case IDLE_MODE:
				break;
			case PIN_MODE:
				currentInput.setLength(0);
				setEcho("");
				break;
			case AMOUNT_MODE:
				currentInput.setLength(0);
				setEcho("0.00");
				break;
			case MENU_MODE:
				getToolkit().beep();
				break;
		}
	}

	/**
	 * Handle the CANCEL KEY
	 */
	private synchronized void cancelKeyPressed() {
		switch (mode) {
			case IDLE_MODE:
				synchronized (envelopeAcceptor) {
					envelopeAcceptor.notify();
				}

			case PIN_MODE:
			case AMOUNT_MODE:
			case MENU_MODE:
				cancelled = true;
				notify();
		}
	}


	private void setEcho(String echo) {
		display.setEcho(echo);
	}

	private SimDisplay display;

	private SimEnvelopeAcceptor envelopeAcceptor;

	private int mode;

	private static final int IDLE_MODE = 0;

	private static final int PIN_MODE = Simulation.PIN_MODE;

	private static final int AMOUNT_MODE = Simulation.AMOUNT_MODE;

	private static final int MENU_MODE = Simulation.MENU_MODE;

	private StringBuffer currentInput;

	private boolean cancelled;

	private int maxValue;
}


