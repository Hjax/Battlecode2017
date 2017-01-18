package dodgeBot;

import battlecode.common.*;

public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
    protected static int memory_loc = -1;
	public static MapLocation archonStart, enemyStarts[], enemyPing;
	public static int behaviorType = 0;
	public static boolean isFirst = false;
	public static int dodgeColumnLookup[] = {7, 8, 9, 10, 11, 12, 13, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 7, 8, 9, 10, 11, 12, 13};
	public static int dodgeRowLookup[] = {19, 19, 19, 19, 19, 19, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
	public static int dodgeIndexLookup[][] = {{-1, -1, -1, -1, -1, -1, -1, 305, 306, 307, 308, 309, 310, 311, -1, -1, -1, -1, -1, -1},{-1, -1, -1, -1, -1, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, -1, -1, -1, -1},{-1, -1, -1, -1, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, -1, -1, -1},{-1, -1, -1, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, -1, -1},{-1, -1, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, -1},{-1, -1, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, -1},{-1, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, },{-1, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212},{-1, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193},{-1, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174},{-1, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155},{-1, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136},{-1, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117},{-1, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, },{-1, -1, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, -1, },{-1, -1, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, -1},{-1, -1, -1, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, -1, -1, },{-1, -1, -1, -1, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, -1, -1, -1},{-1, -1, -1, -1, -1, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, -1, -1, -1, -1},{-1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, -1, -1, -1, -1, -1, -1}};
	
    protected static void Init(RobotController RobCon) throws GameActionException{
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	archonStart = rc.getInitialArchonLocations(ally)[0];
    	enemyStarts = rc.getInitialArchonLocations(enemy);
    	int enemyPingIndex = rc.getID() % enemyStarts.length;
    	enemyPing = enemyStarts[enemyPingIndex];
    	System.out.println("Attacking enemy archon number " + enemyPingIndex + " of " + enemyStarts.length);
    	initDodgeRowLookup();
    	initDodgeColumnLookup();
    	initDodgeIndexLookup();
    	if (rc.getType() == RobotType.GARDENER && Globals.getTrainerCount() < Math.floor(Globals.getGardenerCount()/2))
    	{
    		System.out.println(Globals.getTrainerCount());
    		System.out.println(Globals.getGardenerCount());
    		behaviorType = 1;
    	}
    	
    	// this should put a new bot into memory
    	try {
    		memory_loc = Memory.first_free_ally();
    		System.out.print("I got assigned memory loc");
    		System.out.println(memory_loc);
    		Memory.reserveAllyIndex(memory_loc);
    		AllyData me = new AllyData(UnitType.getType(), rc.getLocation(), (int) rc.getHealth(), rc.getRoundNum() % 2 == 0);
			Memory.writeAllyData(memory_loc, me);
			Globals.incrementUnitCount(me.type, 1);
		} catch (Exception e) {
			System.out.println("Weird error while updating our location in memory");
			e.printStackTrace();
		}
    	
    	try {
			Globals.updateEdges();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    protected static void initDodgeRowLookup()
    {
    	//int index = 0;
    	//int value = 19;
    	//System.out.println(Clock.getBytecodesLeft());
    	//while (index < 312)
    	//{
    		//if (index == 7 || index == 18 || index == 31 || index == 46 || index == 63 || index == 80 || index == 99 || index == 118 || index == 137 || index == 156 || index == 175 || index == 194 || index == 213 || index == 232 || index == 249 || index == 166 || index == 281 || index == 294 || index == 305)
    		//{
    			//value--;
    		//}
    		
    		//dodgeRowLookup[index++] = value;
    		//System.out.println(Clock.getBytecodesLeft());
    	//}
    	
    }
    
    protected static void initDodgeColumnLookup()
    {
    	//int index = 0;
    	//int width = 7;
    	//int value = 7;
    	//while (index < 312)
    	//{
    		//for (value = 10 - (width - 1)/2; value < 10 + (width + 1)/2; value++)
    		//{		
    			//dodgeColumnLookup[index++] = value;
    		//}
    		//if (index == 7)
    		//{
    			//width += 4;
    		//}
    		//if (index == 18 || index == 31 || index == 46 || index == 80)
    		//{
    			//width += 2;
    		//}
    		//if (index == 232 || index == 266 || index == 281 || index == 294)
    		//{
    			//width-= 2;
    		//}
    		//if (index == 305)
    		//{
    			//width -= 4;
    		//}
    	//}
    }
    
    protected static void initDodgeIndexLookup()
    {
    	//int row = 19;
    	//int index = 0;
    	//int column = 0;
    	//int width = 7;
    	//for (row = 19; row >= 0; row--)
    	//{
    		//for (column = 0; column < 20; column++)
        	//{
        		//if (column < 10 - (width - 1)/2 || column > 10 + (width - 1)/2)
        		//{
        			//dodgeIndexLookup[row][column] = -1;
        		//}
        		//else
        		//{
        			//dodgeIndexLookup[row][column] = index++;
        		//}
        	//}
    		//if (index == 7)
    		//{
    			//width += 4;
    		//}
    		//if (index == 18 || index == 31 || index == 46 || index == 80)
    		//{
    			//width += 2;
    		//}
    		//if (index == 232 || index == 266 || index == 281 || index == 294)
    		//{
    			//width-= 2;
    		//}
    		//if (index == 305)
    		//{
    			//width -= 4;
    		//}
    	//}
    }

    
    
    
    protected static void startTurn() throws Exception 
    {
    	if (rc.getTeamBullets() > 1000)
    	{
    		rc.donate(1000);
    	}
    	if (rc.getRoundLimit() - rc.getRoundNum() < 100)
    	{
    		rc.donate(rc.getTeamBullets() - rc.getTeamBullets() % 10);
    	}
    	if (Globals.getRoundNumber() != rc.getRoundNum()){
    		Globals.setRoundNumber(rc.getRoundNum());
    		int start = Clock.getBytecodeNum();
    		Memory.pruneAllyMemory();
    		System.out.print("Defrag took: ");
    		System.out.println(Clock.getBytecodeNum() - start);
    		start = Clock.getBytecodeNum();
    		Memory.pruneOrders();
    		System.out.print("Orders took: ");
    		System.out.println(Clock.getBytecodeNum() - start);
    		isFirst = true;
    	} else {
    		isFirst = false;
    	}
    }
    
    protected static void endTurn() throws GameActionException {
    	AllyData me = Memory.readAlly(memory_loc);
    	me.location = rc.getLocation();
    	me.alive = (rc.getRoundNum() % 2) == 1;
    	me.hp = (int) rc.getHealth();
    	
    	Memory.writeAllyData(memory_loc, me);
		try {
			Globals.updateEdges();
		} catch (Exception e) {
			System.out.println("Error while updating edges");
		}
        Utilities.tryShake();
    	Clock.yield();
    }
    
    public static Direction neo() throws GameActionException
    {
    	int bytecodeUsed = Clock.getBytecodeNum();
    	
    	BulletInfo[] bullets = rc.senseNearbyBullets();
    	double xPressure = (float) (Math.random() - 0.5) * 20;
    	double yPressure = (float) (Math.random() - 0.5) * 20;
    	
    	double pathDistance = 0.0f;
    	double pathOffset = 0.0f;
    	
    	double bulletXVel = 0.0;
    	double bulletYVel = 0.0;
    	
    	double relativeX = 0.0;
    	double relativeY = 0.0;
    	
    	//dodge bullets
    	
    	for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
    	{
    		bulletXVel = bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].getDir().radians);
    		bulletYVel = bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].getDir().radians);
    		
    		relativeX = bullets[bulletCount].getLocation().x - rc.getLocation().x;
    		relativeY = bullets[bulletCount].getLocation().y - rc.getLocation().y;
    		
    		pathOffset = relativeY - (bulletYVel * relativeX/bulletXVel)/(-1/bulletYVel - bulletYVel);
    		pathDistance = relativeX/bulletXVel - pathOffset;
    		
    		if (pathDistance > -0.2)
    		{
    			double timeToDodge = pathDistance/bullets[bulletCount].getSpeed();
    			xPressure +=(bullets[bulletCount].damage * -500 / bulletXVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    			yPressure +=(bullets[bulletCount].damage * 500/bulletYVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    		}
    	}
    	System.out.println("AX: " + xPressure);
    	System.out.println("AY: " + yPressure);
    	
    	//if not gardener or archon stay near enemy bots
    	if (rc.getType() != RobotType.ARCHON && rc.getType() != RobotType.GARDENER)
    	{
    		RobotInfo[] engageBots = rc.senseNearbyRobots(7, enemy);
        	for (int botCount = 0; botCount < engageBots.length; botCount++)
        	{
        		relativeX = engageBots[botCount].getLocation().x - rc.getLocation().x;
        		relativeY = engageBots[botCount].getLocation().y - rc.getLocation().y;
        			
        		if (engageBots[botCount].getType() == RobotType.GARDENER)
        		{
        			//be very attracted to enemy gardeners
        			xPressure += relativeX * 30;
            		yPressure += relativeY * 30;
        		}	
        		else if (engageBots[botCount].getType() == RobotType.ARCHON)
        		{
        			//be very attracted to enemy archons
        			xPressure += relativeX * 10;
            		yPressure += relativeY * 10;
        		}	
        		else
        		{
        			xPressure += relativeX / 40;
            		yPressure += relativeY / 40;
        		}
        		
        	}
    	}
    	System.out.println("BX: " + xPressure);
    	System.out.println("BY: " + yPressure);
    	
    	
    	//don't get too close to other bots
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(5);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].getType() != RobotType.LUMBERJACK)
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    			//gardeners should avoid archons more
    			if (rc.getType() == RobotType.GARDENER && avoidBots[botCount].getType() == RobotType.ARCHON && false)
    			{
    				xPressure += -700 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -700 / (relativeY + Math.copySign(1, relativeY));
    			}
    			else
    			{
    				xPressure += -50 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -50 / (relativeY + Math.copySign(1, relativeY));
    			}
	
    		}
    	}
    	System.out.println("CX: " + xPressure);
    	System.out.println("CY: " + yPressure);
    	
    	//avoid friendly gardeners a lot
    	
    	avoidBots = rc.senseNearbyRobots(2 + rc.getType().bodyRadius);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    		relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    		if (avoidBots[botCount].type == RobotType.GARDENER && avoidBots[botCount].team == rc.getTeam())
    		{
    			xPressure += -1400 / (relativeX + Math.copySign(10,  relativeX));
    			yPressure += -1400 / (relativeY + Math.copySign(10, relativeY));
    		}
    	}
    	System.out.println("DX: " + xPressure);
    	System.out.println("DY: " + yPressure);
    	
    	//if gardener or archon, avoid trees
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0 || rc.getType() == RobotType.ARCHON)
    	{
    		int treeCost = Clock.getBytecodeNum();
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(5);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        			
        		xPressure += -90 * avoidTrees[treeCount].radius / (relativeX + Math.copySign(1,  relativeX));
        		yPressure += -90 * avoidTrees[treeCount].radius / (relativeY + Math.copySign(1, relativeY));
        		
        		System.out.println("treeX: " + xPressure);
        		System.out.println("treeY: " + yPressure);
        	} 
        	treeCost = Clock.getBytecodeNum() - treeCost;
        	System.out.println("tree pathing costs: " + treeCost);
    	}
    	
    	
    	
    	
    	
    	//if gardener, move toward nearest edge
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0)
    	{
    		
    		double distFromLowBound = 1000;
    		if (Globals.getBottomEdge() != -1)
    			{distFromLowBound = rc.getLocation().y - Globals.getBottomEdge();}
    		double distFromHighBound = 1000;
    		if (Globals.getTopEdge() != -1)
    			{distFromHighBound = Globals.getTopEdge() - rc.getLocation().y;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			if (distFromHighBound < 4)
    				{yPressure += -30;}
    			else if (distFromHighBound > 5)
    				{yPressure += 150;}	
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			if (distFromLowBound < 4)
    				{yPressure += 30;}
    			else if (distFromLowBound > 5)
    				{yPressure += -150;}	
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		distFromLowBound = 1000;
    		if (Globals.getLeftEdge() != -1)
				{distFromLowBound = rc.getLocation().x - Globals.getLeftEdge();}
    		distFromHighBound = 1000;
    		if (Globals.getRightEdge() != -1)
				{distFromHighBound = Globals.getRightEdge() - rc.getLocation().x;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			System.out.println("right edge at " + Globals.getRightEdge());
    			if (distFromHighBound < 4)
    				{xPressure += -30;}
    			else if (distFromHighBound > 5)
    				{xPressure += 150;}	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			System.out.println("left edge at " + Globals.getLeftEdge());
    			System.out.println("D2startX: " + xPressure);
    			if (distFromLowBound < 4)
    				{xPressure += 30;}
    			else if (distFromLowBound > 5)
    				{xPressure += -150;}	
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	//if archon or lumberjack, avoid edges
    	if (rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.LUMBERJACK)
    	{
    		if (Globals.getTopEdge() != -1)
    		{
    			relativeY = Globals.getTopEdge() - rc.getLocation().y;
    			yPressure += -2000/(relativeY * relativeY);
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (Globals.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Globals.getBottomEdge();
    			yPressure += 2000/(relativeY * relativeY);
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		if (Globals.getRightEdge() != -1)
    		{
    			relativeX = Globals.getRightEdge() - rc.getLocation().x;
    			xPressure += -2000/(relativeX * relativeX);	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (Globals.getLeftEdge() != -1)
    		{
    			relativeX = rc.getLocation().x - Globals.getLeftEdge();
    			xPressure += 2000/(relativeX * relativeX);
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	//aggress to enemy spawn if soldier
    	
    	if (rc.getType() != RobotType.GARDENER && rc.getType() != RobotType.ARCHON)
    	{
    		relativeX = enemyPing.x - rc.getLocation().x;
			relativeY = enemyPing.y - rc.getLocation().y;
			
			xPressure += relativeX / 1;
			yPressure += relativeY / 1;
    	}
    	
    	System.out.println("EX: " + xPressure);
    	System.out.println("EY: " + yPressure);
    	
    	bytecodeUsed = Clock.getBytecodeNum() - bytecodeUsed;
    	System.out.println("pathing takes: " + bytecodeUsed);
    			

    	
    	return new Direction((float)xPressure, (float) yPressure);  
    }
}