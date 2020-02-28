package cc.xpbootcamp.code_smell_kit.$20_large_class;

import java.util.Scanner;
///large class
public class GameController
{
	private Scanner input;
	private String selection;
	private int numInput;
	private int count;
	private char selected;
	private ArcticTrack arctic;
	private DesertTrack desert;
	private Sports sport;
	private SUV suv;
	private boolean turnDone;
	private boolean resumePlay = true;


	// Constructor for GameController class
	public GameController()
	{
		desert = new DesertTrack();
		arctic = new ArcticTrack();
		sport = new Sports();
		suv = new SUV();
	}

	// Interpret the user's selection for turn
	public void suvturnSelect(char select)
	{
		if (Debug.on == true)
		{
			System.out.println("\n'suvturnSelect' method executed");
			System.out.println("Blizzard status is " + arctic.getblizzardStatus());
			System.out.println();
		}
		int suvDistance;
		switch(select)
		{
			case 'a':
			case 'A':
				if (arctic.getblizzardStatus() == true && suv.isEmpty() == false)
				{
					suv.allwheelDrive();
					suvDistance = suv.move();
					arctic.suvLocation(suv, suvDistance);
					arctic.setblizzardStatus(false);
					suv.setsuvDefault();
					System.out.println("\nA blizzard has hit the Arctic track");
				}
				else if (arctic.getblizzardStatus() == false && suv.isEmpty() == false)
				{
					suvDistance = suv.move();
					arctic.suvLocation(suv, suvDistance);
				}
				else if (suv.isEmpty() == true)
					System.out.println("SUV is out of fuel and is unable to drive");
				break;
			case 'c':
			case 'C':
				cheatMenu();
				break;
			case 'd':
			case 'D':
				if (arctic.getblizzardStatus() == true && suv.isEmpty() == false)
				{
					suv.spinWheels();
					suvDistance = suv.move();
					arctic.suvLocation(suv, suvDistance);
					arctic.setblizzardStatus(false);
					suv.setsuvDefault();
					System.out.println("\nA blizzard has hit the Arctic track");
				}
				else if (arctic.getblizzardStatus() == false && suv.isEmpty() == false)
				{
					suvDistance = suv.move();
					arctic.suvLocation(suv, suvDistance);
				}
				else if (suv.isEmpty() == true)
					System.out.println("SUV is out of fuel and is unable to drive");
				break;
			case 'q':
			case 'Q':
				resumePlay = false;
				turnDone = true;
				System.out.println("\nRace has been prompted to quit before finish: a DRAW is declared");
				break;
			default:
				System.out.print("Please enter a valid selection from the menu: ");
				selection = input.next();
				selected = selection.charAt(0);
				suvturnSelect(selected);
		}
	}

	// Interpret the user's selection for turn and implement
	public void sportsturnSelect(char select)
	{
		if (Debug.on == true)
		{
			System.out.println("\n'sportturnSelect' method executed");
			System.out.println("Heatwave status is " + desert.getheatwaveStatus());
			System.out.println();
		}
		int sportDistance;
		switch(select)
		{
			case 'c':
			case 'C':
				cheatMenu();
				break;
			case 'd':
			case 'D':
				if (desert.getheatwaveStatus() == true && sport.isEmpty() == false)
				{
					sport.overHeat();
					sportDistance = sport.move();
					desert.sportLocation(sport, sportDistance);
					arctic.setblizzardStatus(false);
					sport.setsportDefult();
					System.out.println("\nA heatwave has hit the Desert track");
				}
				else if (desert.getheatwaveStatus() == false && sport.isEmpty() == false)
				{
					sportDistance = sport.move();
					desert.sportLocation(sport, sportDistance);
				}
				else if (sport.isEmpty() == true)
					System.out.println("Sports car is out of fuel and unable to drive");
				break;
			case 'q':
			case 'Q':
				resumePlay = false;
				turnDone = true;
				System.out.println("\nRace has been prompted to quit before finish: a DRAW is declared");
				break;
			default:
				System.out.print("Please enter a valid selection from the menu: ");
				selection = input.next();
				selected = selection.charAt(0);
				sportsturnSelect(selected);
		}
	}

	// Displays SUV controls
	public void suvControls()
	{
		System.out.println("\nSUV driving controls");
		System.out.println("(a)ll wheel drive mode");
		System.out.println("(d)rive normally");
		System.out.println("(q)uit game");
		System.out.print("Enter your selection: ");

		selection = input.next();
		selected = selection.charAt(0);
		suvturnSelect(selected);
	}

	// Displays Sports car controls
	public void sportsControls()
	{
		if (resumePlay == true)
		{
			System.out.println("\nSports car driving controls");
			System.out.println("(d)rive normally");
			System.out.println("(q)uit game");
			System.out.print("Enter your selection: ");

			selection = input.next();
			selected = selection.charAt(0);
			sportsturnSelect(selected);
		}
	}

	// Displays cheat menu
	public void cheatMenu()
	{
		System.out.println("\nCHEAT MENU");
		System.out.println("(0) Toggle debug messages on/off");
		System.out.println("(1) Modify sports car fuel amount");
		System.out.println("(2) Modify SUV fuel amount");
		System.out.println("(3) Modify location of sports car");
		System.out.println("(4) Modify location of SUV");
		System.out.println("(5) Create a blizzard in the arctic track");
		System.out.println("(6) Create a heatwave in the desert track");
		System.out.print("Enter your selection: ");

		selection = input.next();
		selected = selection.charAt(0);
		cheatMode(selected);
	}

	// Implement user's cheat code choice
	public void cheatMode(char cheatCode)
	{
		switch(cheatCode)
		{
			case '0':
				if (Debug.on == true)
				{
					Debug.on = false;
					System.out.println("\nDebugging mode is now off");
				}
				else
				{
					Debug.on = true;
					System.out.println("\nDebugging mode is now on, messages displayed correlate to methods");
				}
				break;
			case '1':
				sportFuel();
				break;
			case '2':
				suvFuel();
				break;
			case '3':
				sportTransport();
				break;
			case '4':
				suvTransport();
				break;
			case '5':
				makeBlizzard();
				break;
			case '6':
				makeheatWave();
				break;
			default:
				System.out.print("Please enter a valid selection from the menu: ");
				selection = input.next();
				selected = selection.charAt(0);
				cheatMode(selected);
		}
	}

	// Set fuel for sports car in cheat menu
	public void sportFuel()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'suvFuel' method executed\n");
		}

		System.out.print("Set new fuel level for sports car (values must be greater than or equal to 0): ");
		numInput = input.nextInt();
		sport.setFuel(numInput);
		if (sport.isEmpty() == true)
		{
			System.out.println("\nSports car is out of fuel and is unable to drive");
		}
	}

	// Set fuel for SUV in cheat menu
	public void suvFuel()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'suvFuel' method executed\n");
		}

		System.out.print("Set new fuel level for SUV (values must be greater than or equal to 0): ");
		numInput = input.nextInt();
		suv.setFuel(numInput);
		if (suv.isEmpty() == true)
		{
			System.out.println("\nSUV is out of fuel and is unable to drive");
		}
	}

	// Set location of sports car in cheat menu
	public void sportTransport()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'sportTransport' method executed\n");
		}

		int current = desert.getsportCurrent();
		desert.getTrack()[current] = null;

		System.out.println("\nMoving car...");
		System.out.print("Enter a new position for car from location 0 to 24: ");
		numInput = input.nextInt();

		desert.setLocation(sport, numInput);
		desert.setsportCurrent(numInput);
	}

	// Set lcoation of SUV in cheat menu
	public void suvTransport()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'suvTransport' method executed\n");
		}

		int current = arctic.getsuvCurrent();
		arctic.getTrack()[current] = null;

		System.out.println("\nMoving car...");
		System.out.print("Enter a new position for car from location 0 to 24: ");
		numInput = input.nextInt();

		arctic.setLocation(suv, numInput);
		arctic.setsuvCurrent(numInput);
	}

	// Make a blizzard in the arctic track
	public void makeBlizzard()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'makeBlizzard' method executed");
			System.out.println("Blizzard status is " + arctic.getblizzardStatus());
			System.out.println();
		}
		arctic.setblizzardStatus(true);
		System.out.println("\nA blizzard is forming in the arctic track");

	}

	// Make a heatwave in the desert track
	public void makeheatWave()
	{
		if (Debug.on == true)
		{
			System.out.println("\n'makeheatWave' method executed");
			System.out.println("Blizzard status is " + desert.getheatwaveStatus());
			System.out.println();
		}

		desert.setheatwaveStatus(true);
		System.out.println("\nA heatwave is forming in the desert track");

	}

	// Checks race completion
	public void raceComplete()
	{
		if (arctic.getsuvCurrent() >= 24 && desert.getsportCurrent() >= 24)
		{
			System.out.println("\nCars have reached the end at the same time, a DRAW is declared");
			resumePlay = false;
			turnDone = true;
		}
		else if (arctic.getsuvCurrent() >= 24)
		{
			System.out.println("\nSports car has lost");
			System.out.println("SUV has won the race!");
			resumePlay = false;
			turnDone = true;
		}
		else if (desert.getsportCurrent() >= 24)
		{
			System.out.println("\nSUV has lost");
			System.out.println("Sports car has won the race!");
			resumePlay = false;
			turnDone = true;
		}
		if (suv.isEmpty() == true && sport.isEmpty() == true)
		{
			System.out.println("\nBoth SUV and Sports car are out of fuel are unable to drive: a DRAW is declared");
			resumePlay = false;
			turnDone = true;
		}
	}

	// The content of a turn
	public void aTurn()
	{
		raceComplete();
		System.out.println("\nARCTIC TRACK");
		arctic.display();
		System.out.println("DESERT TRACK");
		desert.display();
		suvControls();
		sportsControls();
		raceComplete();
	}

	// Executes turns until game is over
	public void executeTurn()
	{
		input = new Scanner(System.in);

		arctic.start(suv);
		desert.start(sport);

		count = 0;

		while (resumePlay == true)
		{
			while (count <= 1)
			{
				aTurn();
				if (turnDone == true)
					count = 3;
				else
					count++;
			}
			// Resets the default values every third turn
			if (count <= 2)
			{
				raceComplete();
				System.out.println("\nARCTIC TRACK");
				arctic.display();
				System.out.println("DESERT TRACK");
				desert.display();
				arctic.arcticCondition();
				suvControls();
				desert.desertCondition();
				sportsControls();
				raceComplete();
				if (turnDone == true)
					count = 3;
				else
					count = 0;
			}
		}
		// Final display of tracks
		System.out.println("\nARCTIC TRACK");
		arctic.display();
		System.out.println("DESERT TRACK");
		desert.display();
	}
}
