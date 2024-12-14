
package pacman;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener , KeyListener{
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX=0;
        int velocityY=0;

        Block(Image image, int x, int y, int width, int height){
            this.image=image;
            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
            this.startX=x;
            this.startY=y;
        }
        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction=direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall: walls){
                if(collision(this, wall)){//here this refers to pacman
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX=0;
                    this.velocityY=-tileSize/4;
                }
                case 'D' -> {
                    this.velocityX=0;
                    this.velocityY=tileSize/4;
                }
                case 'L' -> {
                    this.velocityX=-tileSize/4;
                    this.velocityY=0;
                }
                case 'R' -> {
                    this.velocityX=tileSize/4;   
                    this.velocityY=0;
                }
                default -> {
                }
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private final Image wallImage;
    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food

    //Ghosts: b = blue, o = orange, p = pink, r = red

    private final String[] tileMap = {

        "XXXXXXXXXXXXXXXXXXX",

        "X        X        X",

        "X XX XXX X XXX XX X",

        "X                 X",

        "X XX X XXXXX X XX X",

        "X    X       X    X",

        "XXXX XXXX XXXX XXXX",

        "OOOX X       X XOOO",

        "XXXX X XXrXX X XXXX",

        "O       bpo       O",

        "XXXX X XXXXX X XXXX",

        "OOOX X       X XOOO",

        "XXXX X XXXXX X XXXX",

        "X        X        X",

        "X XX XXX X XXX XX X",

        "X  X     P     X  X",

        "XX X X XXXXX X X XX",

        "X    X   X   X    X",

        "X XXXXXX X XXXXXX X",

        "X                 X",

        "XXXXXXXXXXXXXXXXXXX" 

    };

    HashSet<Block> walls;//traversal is easy in hashset//
    HashSet<Block> foods;//no duplicate posible in hashset//
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U','D','L','R'};//directions for ghosts
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load image//
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();


        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        
        loadMap();
        for(Block ghost: ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        //how long it takes to start timer, miliseconds gone between frames
        gameLoop = new Timer(50, this);//20fps(1000/50)
        gameLoop.start();

    }

    public void loadMap(){
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for(int r=0; r<rowCount; r++){
            for(int c=0; c<columnCount; c++){
                String row = tileMap[r];//extract the first row///
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                switch (tileMapChar) {
                    case 'X' -> {
                        Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                        walls.add(wall);
                    }
                    case 'b' ->                         {
                            Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                            ghosts.add(ghost);
                        }
                    case 'o' ->                         {
                            Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                            ghosts.add(ghost);
                        }
                    case 'p' ->                         {
                            Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                            ghosts.add(ghost);
                        }
                    case 'r' ->                         {
                            Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                            ghosts.add(ghost);
                        }
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> {
                        Block food = new Block(null, x+14, y+14, 4, 4);
                        foods.add(food);
                    }
                    default -> {
                    }
                }
            }
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x, pacman.y, pacman.width, pacman.height,null);
        
        for(Block ghost: ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall: walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for(Block food : foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        //score
        g.setFont(new Font("Raleway",Font.BOLD,18));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collision
        for(Block wall : walls){
            if(collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
        
        //check ghost collisions
        for(Block ghost : ghosts){
            if(collision(ghost, pacman)){
                lives -= 1;
                if(lives == 0){
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if(ghost.y ==tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for(Block wall : walls){
                if(collision(ghost, wall) || ghost.x <=0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //check food collision
        Block foodEaten = null;
        for(Block food : foods){
            if(collision(pacman, food)){
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
        //if all the foods are eaten 
        if(foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b){
        return  a.x < b.x + b.width && 
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions(){
        pacman.reset();
        pacman.velocityX = 0;//dont want to move pacman untill user hits some key//
        pacman.velocityY = 0;
        for(Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();//first we update position.//
        repaint();//then we redraw//
        if(gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {//key release korar age kaj korbe na//
        //System.out.println("KeyEvent"+e.getKeyCode());
        if(gameOver){
            loadMap();//need to load map to load the foods//
            resetPositions();
            lives = 3;
            score =0;
            gameOver = false;
            gameLoop.start();
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> pacman.updateDirection('U');
            case KeyEvent.VK_S -> pacman.updateDirection('D');
            case KeyEvent.VK_A -> pacman.updateDirection('L');
            case KeyEvent.VK_D -> pacman.updateDirection('R');
            default -> {
            }
        }

        switch (pacman.direction) {
            case 'U':
                pacman.image = pacmanUpImage;
                break;
            case 'D':
                pacman.image = pacmanDownImage;
                break;
            case 'L':
                pacman.image = pacmanLeftImage;
                break;
            case 'R':
                pacman.image = pacmanRightImage;
                break;
            default:
                break;
        }
    }
}

