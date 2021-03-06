import com.ibm.coderally.agent.DefaultCarAIAgent;
import com.ibm.coderally.api.agent.AIUtils;
import com.ibm.coderally.entity.cars.agent.Car;
import com.ibm.coderally.entity.obstacle.agent.Obstacle;
import com.ibm.coderally.geo.agent.CheckPoint;
import com.ibm.coderally.track.agent.Track;

public class Pedepano extends DefaultCarAIAgent {

	@Override
	public void onCarCollision(Car other) {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onCheckpointUpdated(CheckPoint oldCheckpoint) {
		// Replace with custom logic or remove method for default implementation.
		getCar().setBrakePercent(0);
		getCar().setAccelerationPercent(100);
		getCar().setTarget(AIUtils.getClosestLane(getCar().getCheckpoint(), getCar().getPosition()));
	}

	@Override
	public void onObstacleInProximity(Obstacle obstacle) {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onOffTrack() {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onOpponentInProximity(Car car) {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onRaceStart() {

		// Replace with custom logic or remove method for default implementation.

		getCar().setBrakePercent(0);
		getCar().setAccelerationPercent(100);
		getCar().setTarget(AIUtils.getClosestLane(getCar().getCheckpoint(), getCar().getPosition()));
		
	}

	@Override
	public void onTimeStep() {
		// Replace with custom logic or remove method for default implementation.
		
		AIUtils.recalculateHeading(getCar());
	}

	@Override
	public void init(Car car, Track track) {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onObstacleCollision(Obstacle obstacle) {
		// Provide custom logic or remove method for default implementation.		
	}

	@Override
	public void onStalled() {
		// Provide custom logic or remove method for default implementation.		
	}

}
