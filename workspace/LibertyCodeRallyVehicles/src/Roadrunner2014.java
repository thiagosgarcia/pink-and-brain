import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ibm.coderally.agent.DefaultCarAIAgent;
import com.ibm.coderally.api.agent.AIUtils;
import com.ibm.coderally.api.ai.agent.CheckpointAI;
import com.ibm.coderally.entity.cars.agent.Car;
import com.ibm.coderally.entity.obstacle.agent.Obstacle;
import com.ibm.coderally.geo.agent.CheckPoint;
import com.ibm.coderally.geo.agent.Point;
import com.ibm.coderally.track.agent.PolePosition;
import com.ibm.coderally.track.agent.Track;

public class Roadrunner2014 extends DefaultCarAIAgent {

    Status carStatus = null;
    float[] slopes;

    public Roadrunner2014() {
    }
    
    public static String teste = "";

    @Override
    public void onRaceStart() {
	drive(Action.onRaceStart);
    }

    @Override
    public void onCheckpointUpdated(CheckPoint oldCheckpoint) {
	drive(Action.onCheckpointUpdated);
    }

    @Override
    public void onOffTrack() {
	drive(Action.onOffTrack);
    }

    @Override
    public void onCarCollision(Car other) {
	drive(Action.onCarCollision);
    }

    @Override
    public void onOpponentInProximity(Car car) {
	drive(Action.onOpponentInProximity);
    }

    @Override
    public void onObstacleInProximity(Obstacle obstacle) {
	drive(Action.onObstacleInProximity);
    }

    @Override
    public void onTimeStep() {
	drive(Action.onTimeStep);
    }

    @Override
    public void onStalled() {
	drive(Action.onStalled);
    }

    @Override
    public void onObstacleCollision(Obstacle obstacle) {
	drive(Action.onObstacleCollision);
    }

    @Override
    public void init(Car car, Track track) {
	super.init(car, track);
	raceStart();
	drive(Action.onRaceStart);
	setStatus(Status.Running);
	Log.logRaceInit(track, car);
    }

    public Status getStatus() {
	return carStatus == null ? Status.Running : carStatus;
    }

    public void setStatus(Status carStatus) {
	if (this.carStatus != carStatus)
	    Log.log("new status: " + carStatus);
	this.carStatus = carStatus;
    }

    private Car car() {
	return getCar();
    }

    private Track track() {
	return getTrack();
    }

    Action action;
    Action lastAction;
    Status status;

    public void drive(Action action) {
	switch (action) {
	case onRaceStart:
	    raceStart();
	    checkpointUpdated();
	    break;
	case onCheckpointUpdated:
	    checkpointUpdated();
	    break;
	case onStalled:
	    move(CarUtil.checkPoint(car(), track(), 1).getCenter(), 100);
	case onTimeStep:
	    runForestRun();
	    break;
	default:
	    checkpointUpdated();

	}
	lastAction = action;
    }

    private void raceStart() {
	slopes = CarUtil.calculateSlopes(getTrack());
    }

    private boolean lowSpeed() {
	if (CarUtil.normalizedSpeed(car()) < 60)
	    return true;
	return false;
    }

    private void runForestRun() {
	if (lowSpeed()) {
	    move(AIUtils.getClosestLane(car().getCheckpoint(), car()
		    .getPosition()), 100);
	    Log.log("RunForest " + CarUtil.normalizedSpeed(car()));
	}
    }

    private void checkpointUpdated() {
	runForestRun();
	if (slopes == null)
	    slopes = CarUtil.calculateSlopes(track());

	float speed = CarUtil.normalizedSpeed(car());
	
	int maxAhead = 3;
	if(speed < 80)
	    maxAhead = 2;
	else if(speed < 105)
	    maxAhead = 3;
//	else if(speed < 110)
//	    maxAhead = 4;
//	else if(speed < 150)
//	    maxAhead = 5;
	
	int acc = 0;
	float slopeAhead = 0F;
	float slopeHere = 0F;
	for (int i = 2; i < maxAhead; i++)
	    slopeAhead += slopes[CarUtil.checkpointIndex(car(), track(), i)];

	for (int i = 0; i < 3; i++)
	    slopeHere += slopes[CarUtil.checkpointIndex(car(), track(), i)];
	
	if (slopeAhead > 1000)
	    slopeAhead = 0;
	
	if(slopeHere > 150)
	    slopeHere = 0;
	
	acc = (int) (100F - ( slopeHere * 2.5F ) - slopeAhead ) ;
	//acc = (int) (100F - (slopeAhead * 10F) + (150 - slopeHere) ) ;

	Log.log("Acc : " + acc + " | slope - Ahead: " + slopeAhead + " Here: " + slopeHere + " | speed: " + speed);
	move(AIUtils.getClosestLane(car().getCheckpoint(), car().getPosition()),
		acc);
    }

    enum Action {
	onRaceStart, onCheckpointUpdated, onOffTrack, onCarCollision, onOpponentInProximity, onTimeStep, onStalled, onObstacleCollision, onObstacleInProximity
    }

    enum Target {
	Keep, Closest, Alternative, Center
    }

    enum Status {
	Running, Avoiding, Aligning, Attacking, Winning
    }

    private void speed(int percent) {
	if (percent < 0) {
	    getCar().setBrakePercent(Math.abs(percent));
	    getCar().setAccelerationPercent(0);
	} else {
	    getCar().setAccelerationPercent(percent);
	    getCar().setBrakePercent(0);
	}
    }

    private void move(Point t, int percent) {
	getCar().setTarget(t);
	speed(percent);
    }

    static class Log {
	static DateFormat df = new SimpleDateFormat("HH:mm:ss");
	static FileWriter writer;
	static File f;

	public static void log(String s, boolean console, boolean file) {
	    if (file)
		write(s);
	    if (console)
		System.out.println(s);
	}

	public static void log(String s) {
	    log(s, true, true);
	}

	private static void write(String s) {
	    String fileName = new SimpleDateFormat("yyyyMMddHHmmSS")
		    .format(Calendar.getInstance().getTime());
	    if (f == null) {
		try {
		    f = File.createTempFile("CodeRally_" + fileName, ".txt");
		    if (!f.exists())
			f.createNewFile();
		    System.out.println("File: " + fileName);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    try {
		if (f != null) {
		    writer = new FileWriter(f, true);
		    BufferedWriter bufferWritter = new BufferedWriter(writer);
		    bufferWritter.write(df.format(Calendar.getInstance()
			    .getTime()) + "> " + s + "\n");
		    bufferWritter.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	private static void logRaceInit(Track track, Car car) {
	    StringBuffer s = new StringBuffer("");
	    s.append("\nCheckpoints: ");
	    for (CheckPoint c : track.getCheckpoints())
		s.append(c + " <|> " + c.getStart() + " | " + c.getCenter()
			+ " | " + c.getEnd() + "\n");
	    s.append("\nmaxCars" + track.getTrackData().getMaxCars());
	    for (PolePosition p : track.getTrackData().getPolePositions())
		s.append(p.toString() + " " + p.getPosition() + " , ");
	    s.append("\nStarting line:");
	    s.append(track.getTrackData().getStartingLine().toString()
		    + " <|> ");
	    s.append(track.getTrackData().getStartingLine().getStart()
		    .toString()
		    + " | ");
	    s.append(track.getTrackData().getStartingLine().getCenter()
		    .toString()
		    + " | ");
	    s.append(track.getTrackData().getStartingLine().getEnd().toString());
	    s.append("\n" + car.getUser());
	    log(s.toString());
	}
    }

    static class CarUtil {
	static float normalizedSpeed(Car car) {
	    return car.getVelocity().normalize();// * 1.609344F;
	}

	static float[] calculateSlopes(Track track) {
	    float[] slopes = new float[track.getCheckpoints().size()];
	    for (int i = 0; i < track.getCheckpoints().size(); i++) {
		int j = i + 1 >= track.getCheckpoints().size() ? 0 : i + 1;

		CheckPoint c = track.getCheckpoints().get(i);
		CheckPoint c2 = track.getCheckpoints().get(j);

		slopes[i] = Math.abs(CarUtil.slope(c.getCenter(),
			c2.getCenter()));
	    }
	    return slopes;
	}

	static int checkpointIndex(Car car, Track track, int ahead) {
	    return track.getCheckpoints()
		    .indexOf(checkPoint(car, track, ahead));
	}

	static CheckPoint checkPoint(Car car, Track track, int ahead) {
	    int i = ahead + track.getCheckpoints().indexOf(car.getCheckpoint());
	    if (i >= track.getCheckpoints().size())
		i -= track.getCheckpoints().size();
	    return track.getCheckpoints().get(i);
	}

	// coeficiente angular
	static float slope(Point p1, Point p2) {
	    try {
		return (float) (p2.getIntY() - p1.getIntY())
			/ (p2.getIntX() - p1.getIntX());
	    } catch (Exception ex) {
		return 0F;
	    }
	}

	static TurnOrientation turnTo(Point p1, Point p2) {
	    Orientation o = orientation(p1, p2);
	    if (p2.getIntX() > p1.getIntX()) {
		switch (o) {
		case Northeast:
		case East:
		case Southeast:
		    return TurnOrientation.Right;
		case Southwest:
		case West:
		case Northwest:
		    return TurnOrientation.Left;
		default:
		    return TurnOrientation.Straight;
		}
	    } else if (p2.getIntX() < p1.getIntX()) {
		switch (o) {
		case Southwest:
		case West:
		case Northwest:
		    return TurnOrientation.Right;
		case Northeast:
		case East:
		case Southeast:
		    return TurnOrientation.Left;
		default:
		    return TurnOrientation.Straight;
		}
	    }
	    return TurnOrientation.Straight;
	}

	enum TurnOrientation {
	    Left, Right, Straight
	}

	static Orientation orientation(Point p1, Point p2) {
	    Orientation ns = northSouth(p1, p2);
	    Orientation ew = eastWest(p1, p2);
	    if (ns == Orientation.Stopped)
		return ew;
	    if (ew == Orientation.Stopped)
		return ns;
	    switch (ns) {
	    case North:
		if (ew == Orientation.East)
		    return Orientation.Northeast;
		return Orientation.Northwest;
	    case South:
		if (ew == Orientation.East)
		    return Orientation.Southeast;
		return Orientation.Southwest;
	    default:
		// THIS WILL NOT HAPPEN
		return Orientation.Stopped;
	    }
	}

	private static Orientation northSouth(Point p1, Point p2) {
	    int xDiff = p2.getIntX() - p1.getIntX();

	    if (xDiff > 0)
		return Orientation.North;
	    if (xDiff < 0)
		return Orientation.South;
	    return Orientation.Stopped;
	}

	private static Orientation eastWest(Point p1, Point p2) {
	    int yDiff = p2.getIntY() - p2.getIntY();

	    if (yDiff > 0)
		return Orientation.East;
	    if (yDiff < 0)
		return Orientation.West;
	    return Orientation.Stopped;
	}

	enum Orientation {
	    North, South, East, West, Northeast, Southeast, Southwest, Northwest, Stopped
	}
    }

    public void target(Target t) {
	switch (t) {
	default: // Keep
	    break;
	case Closest:
	    getCar().setTarget(
		    AIUtils.getClosestLane(getCar().getCheckpoint(), getCar()
			    .getPosition()));
	    break;
	case Alternative:
	    getCar().setTarget(
		    AIUtils.getAlternativeLane(getCar().getCheckpoint(),
			    getCar().getPosition()));
	    break;
	case Center:
	    getCar().setTarget(getCar().getCheckpoint().getCenter());
	    break;
	}
    }

}
