package team097;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import battlecode.common.*;

public class Movement {
	
	static Random randomThing = new Random();
	static Direction allDirections[] = Direction.values();
	enum movementState {
		noObstacle, foundObstacle, foundDeadEnd
	}
	static movementState dirFlag = movementState.noObstacle;
	static Direction currentDir;
	
	
	
	public static void moveRobotRandomly(RobotController rc) throws GameActionException 
	{
		int randomMovement = (int)(randomThing.nextDouble()*12);
		if(randomMovement < 9) {
			Direction chosenDir = allDirections[randomMovement];
			if(rc.isActive() && rc.canMove(chosenDir)) {
				rc.move(chosenDir);
			} //end inner if
		} //end outer if
		else {
			Direction towardsEnemyHQ = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if(rc.isActive() && rc.canMove(towardsEnemyHQ)) {
				rc.move(towardsEnemyHQ);
			}//end inner if
		} //end else
	}
	
	public static void moveRobotRandomlyTowardsEnemyPastr(RobotController rc) throws GameActionException 
	{
		int randomMovement = (int)(randomThing.nextDouble()*12);
		if(randomMovement < 9) 
		{
			Direction chosenDir = allDirections[randomMovement];
			if(rc.isActive() && rc.canMove(chosenDir)) {
				rc.move(chosenDir);
			} //end inner if
		} //end outer if
		else if (rc.sensePastrLocations(rc.getTeam().opponent()).length > 0)
		{
			Direction towardsEnemyPastr = rc.getLocation().directionTo(rc.sensePastrLocations(rc.getTeam().opponent())[0]);
			if(rc.isActive() && rc.canMove(towardsEnemyPastr)) {
				rc.move(towardsEnemyPastr);
			}//end inner if
		} 
		else 
		{
			Direction towardsEnemyHQ = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if(rc.isActive() && rc.canMove(towardsEnemyHQ)) {
				rc.move(towardsEnemyHQ);
			}//end inner if
		} //end else
	}
	
	//Currently craptastic
	public static void moveTowardsLocationBuglike(RobotController rc, MapLocation destination) throws GameActionException 
	{
		Direction chosenDir = rc.getLocation().directionTo(destination);
		if(rc.isActive())
		{
			
			if (dirFlag == movementState.noObstacle)
			{
				if(rc.canMove(chosenDir)) 
				{
					rc.move(chosenDir);
					dirFlag = movementState.noObstacle;
				}
				if(rc.canMove(chosenDir.rotateLeft()))
				{
					rc.move(chosenDir.rotateLeft());
				}
				else if(rc.canMove(chosenDir.rotateLeft().rotateLeft()))
				{
					rc.move(chosenDir.rotateLeft().rotateLeft());
				}
				else
				{
					dirFlag = movementState.foundObstacle;
					currentDir = chosenDir;
				}
			}
			
			
			if (dirFlag == movementState.foundObstacle)
			{
				if(rc.canMove(currentDir)) 
				{
					rc.move(currentDir);
					dirFlag = movementState.noObstacle;
				}
				if(rc.canMove(currentDir.rotateRight()))
				{
					dirFlag = movementState.noObstacle;
					rc.move(currentDir.rotateRight());
				}
				else if(rc.canMove(currentDir.rotateRight().rotateRight()))
				{
					rc.move(currentDir.rotateRight().rotateRight());
				}
				else
				{
					dirFlag = movementState.foundDeadEnd;
				}
			}
			
			if (dirFlag == movementState.foundDeadEnd)
			{
				if(rc.canMove(currentDir.rotateLeft()))
				{
					rc.move(currentDir.rotateLeft());
				}
				else if(rc.canMove(currentDir.rotateLeft().rotateLeft()))
				{
					rc.move(currentDir.rotateLeft().rotateLeft());
				}
				else if(rc.canMove(currentDir.rotateLeft().rotateLeft().rotateLeft()))
				{
					rc.move(currentDir.rotateLeft().rotateLeft().rotateLeft());
				}
				else if(rc.canMove(currentDir.rotateLeft().rotateLeft().rotateLeft().rotateLeft()))
				{
					rc.move(currentDir.rotateLeft().rotateLeft().rotateLeft().rotateLeft());
				}
				else if(rc.canMove(currentDir.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft()))
				{
					rc.move(currentDir.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft());
				}
			}
			
			
			//I bet there is actually a somewhat reasonable way to do this with an FSA.  This is not it though.
			//I'm applying the wrong tool here, but I could use some more practice building these anyway.
			//This gets confused easily.  I really want to make it work now though...
		} 
	}
	
	public static ArrayList<MapLocation> pathToGoal(RobotController rc, MapLocation start, MapLocation goal) {
		int mapHeight = rc.getMapHeight();
		int mapWidth = rc.getMapWidth();
		
		MapLocation[][] origins = new MapLocation[mapWidth][mapHeight];
		for(int i=0; i<mapWidth; i++){
			for(int j=0; j<mapHeight; j++){
				origins[i][j] = null;
			}//end inner for
		} //end outer for
		Set<MapLocation>front = new HashSet<MapLocation>();
		
		MapLocation[] adjacentSquares = findAdjacent(start);
		for(int i=0; i < adjacentSquares.length; i++){
			TerrainTile terrainAtLocation = rc.senseTerrainTile(adjacentSquares[i]);
			if(terrainAtLocation == TerrainTile.NORMAL || terrainAtLocation == TerrainTile.ROAD){
				front.add(adjacentSquares[i]);
			}//end if
		}//end for				//done adding starting squares to front
		while(true){
			MapLocation bestOption = findBestOption(front, goal);;
			front.remove(bestOption);
			adjacentSquares = findAdjacent(bestOption);
			for(int i=0; i < adjacentSquares.length; i++){
				TerrainTile terrainAtLocation = rc.senseTerrainTile(adjacentSquares[i]);
				if((terrainAtLocation == TerrainTile.NORMAL || terrainAtLocation == TerrainTile.ROAD) && adjacentSquares[i] != rc.senseHQLocation() && origins[adjacentSquares[i].x][adjacentSquares[i].y] == null){
					front.add(adjacentSquares[i]);
					origins[adjacentSquares[i].x][adjacentSquares[i].y] = bestOption;
				} //end if
			} //end for
			
			if(bestOption.isAdjacentTo(goal)){
				System.out.println("Route calculated!");
				break;
			}//end if
		}//end while
		System.out.println("Route mapped!");
		
		ArrayList<MapLocation> route = new ArrayList<MapLocation>();
		MapLocation currentPosition = goal;
		route.add(currentPosition);
		while(!currentPosition.isAdjacentTo(start)){
			currentPosition = origins[currentPosition.x][currentPosition.y];
			route.add(currentPosition);
		}//end while
		
		Collections.reverse(route); //reverse the arraylist into the expected order
		return route;
	}//end pathToGoal()
	
	private static MapLocation findBestOption(Set<MapLocation> front, MapLocation goal){
		MapLocation bestPosition = null;
		float distanceOfBestPosition = Float.MAX_VALUE;
		for(MapLocation i : front){
			float distance = i.distanceSquaredTo(goal);
			if(distance < distanceOfBestPosition){
				distanceOfBestPosition = distance;
				bestPosition = i;
			}//end if
		}//end for
		
		return bestPosition;
	} //end findBestOption
	
	private static MapLocation[] findAdjacent(MapLocation location){
		MapLocation[] array = new MapLocation[8];
		array[0] = location.add(0,1); //add North
		array[1] = location.add(1,1); //add Northeast
		array[2] = location.add(1,0); //add East
		array[3] = location.add(1,-1); //add Southeast
		array[4] = location.add(0,-1); //add South
		array[5] = location.add(-1,-1); //add Southwest
		array[6] = location.add(-1,0); //add West
		array[7] = location.add(-1,1); //add Northwest
		return array;
	} //end findAdjacent()
	
}
