// Kyle Orcutt - SideScroller game I made in my first year of school
// Object of the game -> survive the asteroids field and collect crystals for points
// Mechanics:
//    - Speed of the game and rate of spawning increases as the level progresses
//    - Game ends when player runs out of shield power OR they reach the end
//    - Shield generators and crystals can be collected along the way
//    - Player can also shoot to clear the asteroids


public class Game
{
    private static Grid grid;
    private int userRow;
    private int msElapsed;
    private int msDivisor;
    private int timesGet = 0;
    private int kilometers = 0;
    private int shield = 3;
    private int gameCount = 0;
    private int score = 0;
    private boolean isShotFired = false;
    private static Game game = new Game();

    public Game()
    {
        grid = new Grid(6, 13);
        userRow = 0;
        msElapsed = 0;
        msDivisor = 0;
        updateTitle();
        grid.setImage(new Location(userRow, 0), "res/ship.gif");
    }

    public void play()
    {
        while (!isGameOver())
        {
            while (gameCount == 0){
                setForMessage();
                showGameStart();
                int key = grid.checkLastKeyPressed();
                if (key > 0){
                    gameCount++;
                    setToNull();
                }
            }
            grid.pause(100);
            handleKeyPress();
            if (kilometers > 0 && kilometers < 7500){
                msDivisor = 300;
            }
            else if (kilometers > 7500 && kilometers < 15000){
                msDivisor = 200;
            }
            else if (kilometers > 15000 && kilometers < 20000){
                msDivisor = 300;
            }
            else{
                msDivisor = 200;
            }
            if (msElapsed % msDivisor == 0)
            {
                scrollLeft();
                populateRightEdge();
            }
            updateTitle();
            msElapsed += 100;
        }
        while (isGameOver()){
            setForMessage();
            if (shield > 0){
                showGameWin();
            }else {
                showGameOver();
            }
            handleKeyPress();
            if (kilometers == 0){
                setToNull();
                test();
            }
        }
    }

    public void handleKeyPress()
    {
        if (!isGameOver()){
            int key = grid.checkLastKeyPressed();
            grid.setImage(new Location(userRow,0), null);

            if (key == 38 && userRow > 0){
                userRow--;
                Location userCollision = new Location(userRow,0);
                handleCollision(userCollision);
            }
            if (key == 40 && userRow < 5){
                userRow++;
                Location userCollision = new Location(userRow,0);
                handleCollision(userCollision);
            }                   
            if (key == 32){
                Location shotSpot = new Location(userRow,1); 
                Location rockSpot = new Location(userRow,2);
                if (grid.getImage(shotSpot) == "res/rock.gif"){
                    grid.setImage(new Location(userRow,1), "res/explode.gif");
                    timesGet += 250;
                    isShotFired = false;
                }
                if (grid.getImage(shotSpot) == null){
                    grid.setImage(new Location(userRow,1), "res/shot.gif");
                    if (grid.getImage(rockSpot) == "res/rock.gif"){
                        isShotFired = true;
                    }
                }
            }
            grid.setImage(new Location(userRow,0), "res/ship.gif");
        }
        else{
            int key = grid.checkLastKeyPressed();
            if (key > 0){
                kilometers = 0;
                shield = 3;
                score = 0;
                timesGet = 0;
                setToNull();
                game.play();
            }
        }
    }

    public void populateRightEdge()
    {
        if (kilometers < 29000){
            int quantumRow = (int)(Math.random() * 6);
            int rockRow = (int)(Math.random() * 6);
            int xRow = (int)(Math.random() * 6);
            int shieldRow = (int)(Math.random() * 6);

            int randomize = 0;
            Location rockSpot = new Location(rockRow,11);
            Location getSpot = new Location(quantumRow,11);
            Location xSpot = new Location(xRow,11);
            Location shieldSpot = new Location(shieldRow,11);

            if (kilometers > 0 && kilometers < 15000){
                randomize = (int)(Math.random() * 100 + 1)*2;
            }
            if (kilometers > 15000){
                randomize = (int)(Math.random() * 100 + 1)*6;
            }

            if (grid.getImage(rockSpot) == null){
                grid.setImage(new Location(rockRow,12), "res/rock.gif");
            }
            if (randomize % 6 == 0 && grid.getImage(getSpot) == null){
                grid.setImage(new Location(quantumRow,12), "res/quantumCrystal.gif");
            }
            if (randomize % 35 == 0  && grid.getImage(xSpot) == null){
                grid.setImage(new Location(xRow,12), "res/quantumCrystal2.gif");
            }
            if (randomize % 37 == 0 && kilometers > 5000 && grid.getImage(shieldSpot) == null){
                grid.setImage(new Location(shieldRow,12), "res/shieldGenerator.gif");
            }
        }
    }

    public void scrollLeft()
    {
        Location scrollCollision = new Location(userRow,1);
        handleCollision(scrollCollision);
        Location shotCollision = new Location(userRow,2);
        handleShot(shotCollision);

        for (int i = 0; i < grid.getNumRows(); i++){
            for (int k = 0; k < grid.getNumCols(); k++){
                Location currentPlace = new Location(i,k);
                travelledKMs();
                if (k > 0 && !currentPlace.equals(userRow) && grid.getImage(currentPlace) != "res/shot.gif"){
                    Location move = new Location(i,k-1);
                    grid.setImage(move, grid.getImage(currentPlace));
                    grid.setImage(new Location(i,k), null);
                }
            }
        }
        grid.setImage(new Location(userRow,0), "res/ship.gif");
    }

    public void handleCollision(Location loc)
    {
        if (grid.getImage(loc) == "res/quantumCrystal.gif"){
            grid.setImage((loc), null);
            timesGet += 1000;
        }
        if (grid.getImage(loc) == "res/rock.gif"){
            grid.setImage((loc), null);
            shield--;
        }
        if (grid.getImage(loc) == "res/quantumCrystal2.gif"){
            grid.setImage((loc), null);
            timesGet += 5000;
        }
        if (grid.getImage(loc) == "res/shieldGenerator.gif"){
            grid.setImage((loc), null);
            if (shield < 3){
                shield++;
            }
        }
    }

    public void handleShot(Location loc){
        if (grid.getImage(loc) == "res/rock.gif" && isShotFired == true){
            grid.setImage((loc), "res/explode.gif");
            timesGet += 250;
            isShotFired = false;
        }
    }

    public int getScore()
    {
        score = timesGet + (kilometers / 10);
        return score;
    }

    public void travelledKMs(){
        kilometers++;
    }

    public void updateTitle()
    {
        grid.setTitle("Score:  " + getScore() + " km: " + kilometers + " shield: " + shield);
    }

    public boolean isGameOver()
    {
        if (kilometers > 30000 || shield < 1){
            return true;
        }else {
            return false;
        }
    }

    public static void test()
    {
        game.play();
    }

    public static void main(String[] args)
    {
        grid.showMessageDialog("Controls: \n Press the up/down arrow keys to move. \n Press the spacebar to shoot. \n Collect crystals/orbs for points and shield boosts. \n" +
            "Avoid/shoot the asteroids!");
        test();
    }

    public void setToNull(){
        for (int i = 0; i < 6; i++){
            for (int k = 0; k < 13; k++){
                grid.setImage(new Location(i,k), null);
            }
        }
    }

    public void setForMessage(){
        for (int i = 4; i < 6; i++){
            for (int k = 0; k < 13; k++){
                grid.setImage(new Location(i,k), null);
            }
        }
        for (int i = 0; i < 4; i++){
            grid.setImage(new Location(i,0), null);
            grid.setImage(new Location(i,1), null);
            grid.setImage(new Location(i,11), null);
            grid.setImage(new Location(i,12), null);
        }
    }

    public void showGameStart(){
        grid.setImage(new Location(0,2), "res/a.gif");
        grid.setImage(new Location(0,3), "res/s.gif");
        grid.setImage(new Location(0,4), "res/t.gif");
        grid.setImage(new Location(0,5), "res/r.gif");
        grid.setImage(new Location(0,6), "res/o.gif");
        grid.setImage(new Location(0,7), "res/s.gif");
        grid.setImage(new Location(0,8), "res/t.gif");
        grid.setImage(new Location(0,9), "res/a.gif");
        grid.setImage(new Location(0,10), "res/r.gif");

        grid.setImage(new Location(1,2), null);
        grid.setImage(new Location(1,3), "res/h.gif");
        grid.setImage(new Location(1,4), "res/i.gif");
        grid.setImage(new Location(1,5), "res/t.gif");
        grid.setImage(new Location(1,6), null);
        grid.setImage(new Location(1,7), "res/a.gif");
        grid.setImage(new Location(1,8), "res/n.gif");
        grid.setImage(new Location(1,9), "res/y.gif");
        grid.setImage(new Location(1,10), null);

        grid.setImage(new Location(2,2), null);
        grid.setImage(new Location(2,3), "res/k.gif");
        grid.setImage(new Location(2,4), "res/e.gif");
        grid.setImage(new Location(2,5), "res/y.gif");
        grid.setImage(new Location(2,6), null);
        grid.setImage(new Location(2,7), null);
        grid.setImage(new Location(2,8), "res/t.gif");
        grid.setImage(new Location(2,9), "res/o.gif");
        grid.setImage(new Location(2,10), null);

        grid.setImage(new Location(3,2), null);
        grid.setImage(new Location(3,3), null);
        grid.setImage(new Location(3,4), "res/p.gif");
        grid.setImage(new Location(3,5), "res/l.gif");
        grid.setImage(new Location(3,6), "res/a.gif");
        grid.setImage(new Location(3,7), "res/y.gif");
        grid.setImage(new Location(3,8), null);
        grid.setImage(new Location(3,9), null);
        grid.setImage(new Location(3,10), null);

    }

    public void showGameOver(){
        grid.setImage(new Location(0,2), "res/g.gif");
        grid.setImage(new Location(0,3), "res/a.gif");
        grid.setImage(new Location(0,4), "res/m.gif");
        grid.setImage(new Location(0,5), "res/e.gif");
        grid.setImage(new Location(0,6), null);
        grid.setImage(new Location(0,7), "res/o.gif");
        grid.setImage(new Location(0,8), "res/v.gif");
        grid.setImage(new Location(0,9), "res/e.gif");
        grid.setImage(new Location(0,10), "res/r.gif");

        grid.setImage(new Location(1,2), null);
        grid.setImage(new Location(1,3), "res/h.gif");
        grid.setImage(new Location(1,4), "res/i.gif");
        grid.setImage(new Location(1,5), "res/t.gif");
        grid.setImage(new Location(1,6), null);
        grid.setImage(new Location(1,7), "res/a.gif");
        grid.setImage(new Location(1,8), "res/n.gif");
        grid.setImage(new Location(1,9), "res/y.gif");
        grid.setImage(new Location(1,10), null);

        grid.setImage(new Location(2,2), null);
        grid.setImage(new Location(2,3), "res/k.gif");
        grid.setImage(new Location(2,4), "res/e.gif");
        grid.setImage(new Location(2,5), "res/y.gif");
        grid.setImage(new Location(2,6), null);
        grid.setImage(new Location(2,7), null);
        grid.setImage(new Location(2,8), "res/t.gif");
        grid.setImage(new Location(2,9), "res/o.gif");
        grid.setImage(new Location(2,10), null);

        grid.setImage(new Location(3,2), "res/t.gif");
        grid.setImage(new Location(3,3), "res/r.gif");
        grid.setImage(new Location(3,4), "res/y.gif");
        grid.setImage(new Location(3,5), null);
        grid.setImage(new Location(3,6), "res/a.gif");
        grid.setImage(new Location(3,7), "res/g.gif");
        grid.setImage(new Location(3,8), "res/a.gif");
        grid.setImage(new Location(3,9), "res/i.gif");
        grid.setImage(new Location(3,10), "res/n.gif");
    }

    public void showGameWin(){
        grid.setImage(new Location(0,2), null);
        grid.setImage(new Location(0,3), "res/y.gif");
        grid.setImage(new Location(0,4), "res/o.gif");
        grid.setImage(new Location(0,5), "res/u.gif");
        grid.setImage(new Location(0,6), null);
        grid.setImage(new Location(0,7), "res/w.gif");
        grid.setImage(new Location(0,8), "res/i.gif");
        grid.setImage(new Location(0,9), "res/n.gif");
        grid.setImage(new Location(0,10), null);

        grid.setImage(new Location(1,2), null);
        grid.setImage(new Location(1,3), "res/h.gif");
        grid.setImage(new Location(1,4), "res/i.gif");
        grid.setImage(new Location(1,5), "res/t.gif");
        grid.setImage(new Location(1,6), null);
        grid.setImage(new Location(1,7), "res/a.gif");
        grid.setImage(new Location(1,8), "res/n.gif");
        grid.setImage(new Location(1,9), "res/y.gif");
        grid.setImage(new Location(1,10), null);

        grid.setImage(new Location(2,2), null);
        grid.setImage(new Location(2,3), "res/k.gif");
        grid.setImage(new Location(2,4), "res/e.gif");
        grid.setImage(new Location(2,5), "res/y.gif");
        grid.setImage(new Location(2,6), null);
        grid.setImage(new Location(2,7), null);
        grid.setImage(new Location(2,8), "res/t.gif");
        grid.setImage(new Location(2,9), "res/o.gif");
        grid.setImage(new Location(2,10), null);

        grid.setImage(new Location(3,2), "res/t.gif");
        grid.setImage(new Location(3,3), "res/r.gif");
        grid.setImage(new Location(3,4), "res/y.gif");
        grid.setImage(new Location(3,5), null);
        grid.setImage(new Location(3,6), "res/a.gif");
        grid.setImage(new Location(3,7), "res/g.gif");
        grid.setImage(new Location(3,8), "res/a.gif");
        grid.setImage(new Location(3,9), "res/i.gif");
        grid.setImage(new Location(3,10), "res/n.gif");

    }
}