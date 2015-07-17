import com.ibm.coderally.agent.SubPackage.QualquerClasse;


public class Skyrunner extends Roadrunner2014 {

public Skyrunner (){
    Log.log(QualquerClasse.QualquerCoisa);
}

//    enum Action {
//	onRaceStart, onCheckpointUpdated, onOffTrack, onCarCollision, onOpponentInProximity, onTimeStep, onStalled, onObstacleCollision, onObstacleInProximity
//    }
//
//    enum Target {
//	Keep, Closest, Alternative, Center
//    }
//
//    enum Status {
//	Running, Avoiding, Aligning, Attacking, Winning
//    }
//
//    @Override
//    public void onRaceStart() {
//	drive(Action.onRaceStart);
//    }
//
//    @Override
//    public void onCheckpointUpdated(CheckPoint oldCheckpoint) {
//	drive(Action.onCheckpointUpdated);
//    }
//
//    @Override
//    public void onOffTrack() {
//	drive(Action.onOffTrack);
//    }
//
//    @Override
//    public void onCarCollision(Car other) {
//	drive(Action.onCarCollision);
//    }
//
//    @Override
//    public void onOpponentInProximity(Car car) {
//	drive(Action.onOpponentInProximity);
//    }
//
//    @Override
//    public void onObstacleInProximity(Obstacle obstacle) {
//	drive(Action.onObstacleInProximity);
//    }
//
//    @Override
//    public void onTimeStep() {
//	drive(Action.onTimeStep);
//    }
//
//    @Override
//    public void onStalled() {
//	drive(Action.onStalled);
//    }
//
//    @Override
//    public void onObstacleCollision(Obstacle obstacle) {
//	drive(Action.onObstacleCollision);
//    }
//
//    @Override
//    public void init(Car car, Track track) {
//	super.init(car, track);
//	raceStart();
//	drive(Action.onRaceStart);
//	setStatus(Status.Running);
//	Log.logRaceInit(track, car);
//    }
//
//    public Status getStatus() {
//	return carStatus == null ? Status.Running : carStatus;
//    }
//
//    public void setStatus(Status carStatus) {
//	if (this.carStatus != carStatus)
//	    Log.log("new status: " + carStatus);
//	this.carStatus = carStatus;
//    }
//
//    private Car car() {
//	return getCar();
//    }
//
//    private Track track() {
//	return getTrack();
//    }
//
//    Action action;
//    Action lastAction;
//    Status status;
//
//    public void drive(Action action) {
//	switch (action) {
//	case onRaceStart:
//	    raceStart();
//	    checkpointUpdated();
//	    break;
//	case onCheckpointUpdated:
//	    checkpointUpdated();
//	    break;
//	case onStalled:
//	    move(CarUtil.checkPoint(car(), track(), 1).getCenter(), 100);
//	case onTimeStep:
//	    runForestRun();
//	    break;
//	default:
//	    checkpointUpdated();
//
//	}
//	lastAction = action;
//    }
}
