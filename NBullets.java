import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.*;
import tester.*;

import java.awt.Color;

//represents a single game piece. i.e. ship, bullet
interface IGamePiece {

  // draws the pieces on the scene
  WorldScene place(WorldScene placePieces);

  // checks if any of that list of game pieces overlap this game piece
  boolean overlapsAny(ILoGamePiece pieces);

  // checks if this game piece is on screen or not
  boolean isOOB(int height, int width);

  // handles moving the piece each tick
  IGamePiece updatePosn();

  // returns whether or not this piece overlaps that piece
  boolean overlapsHelper(IGamePiece piece);

  // adds bullets after explosion and returns new list of them
  ILoGamePiece handleAddNewBullets(ILoGamePiece lst);

  // checks whether or not two pieces overlap by comparing their
  // positions and sizes
  boolean compareHypots(int posnX, int posnY, int size);

  // creates a new list of all the bullets that have to be added
  // to the bullet list after a collision occurs
  ILoGamePiece handleCreateBulletList(ILoGamePiece ilgp, int i);

}

//represents a list of game pieces, i.e. list of ships or list of bullets
interface ILoGamePiece {

  // returns new list of pieces that have not collided with any of 
  // that list of pieces
  ILoGamePiece checkPieceOverlap(ILoGamePiece pieces);

  // checks whether or not any of these pieces overlap with that piece
  boolean overlaps(IGamePiece piece);

  //puts all the pieces onto the game scene
  WorldScene placePieces(WorldScene game);

  //moves the list of pieces each tick
  ILoGamePiece updatePosns();

  // handles pieces that are out of bounds
  ILoGamePiece checkOOB(int height, int width);

  // adds pieces for explosion effect
  ILoGamePiece addNewBullets(IGamePiece firstBullet);

  // creates list of pieces to add to explosion
  ILoGamePiece createBulletList(IGamePiece firstBullet, int i);

  // puts two lists of pieces together
  ILoGamePiece mergeLists(ILoGamePiece addToList);

  // how big is this list?
  int len();
}

// represents an empty list of game pieces
class MtLoGamePiece implements ILoGamePiece {

  MtLoGamePiece(){}

  // returns new list with all of these pieces that do not collide 
  // with those pieces
  public ILoGamePiece checkPieceOverlap(ILoGamePiece pieces) {
    return this;
  }

  //puts all the bullets onto the game scene
  public WorldScene placePieces(WorldScene game) {
    return game;
  }

  // moves each of the game pieces each tick
  public ILoGamePiece updatePosns() {
    return this;
  }

  // handles pieces that are out of bounds
  public ILoGamePiece checkOOB(int height, int width) {
    return this;
  }

  //does that piece overlap any of these pieces?
  public boolean overlaps(IGamePiece piece) {
    return false;
  }

  // adds bullets for explosion effect
  public ILoGamePiece addNewBullets(IGamePiece first) {
    return first.handleAddNewBullets(this);
  }

  // creates list of bullets to add to explosion
  public ILoGamePiece createBulletList(IGamePiece first, int i) {
    return first.handleCreateBulletList(this, i);
  }

  // puts two lists of bullets together
  public ILoGamePiece mergeLists(ILoGamePiece addToList) {
    return addToList;
  }

  // how big is this list?
  public int len() {
    return 0;
  }

}

// represents a non-empty list of game pieces, either ships or bullets
class ConsLoGamePiece implements ILoGamePiece {

  IGamePiece first;
  ILoGamePiece rest;

  ConsLoGamePiece(IGamePiece first, ILoGamePiece rest) {
    this.first = first;
    this.rest = rest;
  }

  //returns list of pieces after being moved (runs each tick)
  public ILoGamePiece updatePosns() {
    IGamePiece first = this.first.updatePosn();
    return new ConsLoGamePiece(first, this.rest.updatePosns());
  }

  //returns new list, removing pieces that are out of bounds
  public ILoGamePiece checkOOB(int height, int width) {
    if (this.first.isOOB(height, width)) {
      return this.rest.checkOOB(height, width);
    }
    return new ConsLoGamePiece(this.first, this.rest.checkOOB(height, width));
  }

  //returns new list with all of these pieces that do not collide 
  // with those pieces
  public ILoGamePiece checkPieceOverlap(ILoGamePiece pieces) {
    if (this.first.overlapsAny(pieces)) {
      return this.rest.checkPieceOverlap(pieces).addNewBullets(this.first);
    }
    return new ConsLoGamePiece(this.first, this.rest.checkPieceOverlap(pieces));
  }

  //does that piece overlap any of these pieces?
  public boolean overlaps(IGamePiece piece) {
    return this.first.overlapsHelper(piece) || this.rest.overlaps(piece);
  }

  //draws all the pieces onto the game scene
  public WorldScene placePieces(WorldScene game) {
    return this.first.place(this.rest.placePieces(game));
  }

  // adds pieces for explosion effect
  public ILoGamePiece addNewBullets(IGamePiece first) {
    return first.handleAddNewBullets(this);
  }

  // puts two lists of pieces together
  public ILoGamePiece mergeLists(ILoGamePiece addToList) {
    return new ConsLoGamePiece(this.first, this.rest.mergeLists(addToList));
  }

  // creates list of bullets (pieces) to add to explosion
  public ILoGamePiece createBulletList(IGamePiece first, int i) {
    return first.handleCreateBulletList(this, i);
  }

  // how big is this list?
  public int len() {
    return 1 + this.rest.len();
  }
}

// represents one abstract game piece, either ship or bullet;
// one spot for several shared functionalities
abstract class AGamePiece implements IGamePiece {
  int posnX;
  int posnY;
  int velocityX;
  int velocityY;
  int size;
  Color color;

  AGamePiece(int posnX, int posnY, int velocityX, int velocityY, int size, Color color) {
    this.posnX = posnX;
    this.posnY = posnY;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    if (size > 10) {
      this.size = 10;
    }
    else {
      this.size = size;
    }
    this.color = color;

  }

  // create physical representation of the AGamePiece
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, this.color);
  }

  // is this AGamePiece out of the screen?
  public boolean isOOB(int height, int width) {
    return (this.posnX - size > width
        || this.posnX + size < 0
        || this.posnY - size > height
        || this.posnY + size < 0);
  }

  // puts the drawing of the game piece onto the game scene
  public WorldScene place(WorldScene game) {
    return game.placeImageXY(this.draw(), this.posnX, this.posnY);
  }

  // does this piece overlap with that piece?
  public boolean overlapsHelper(IGamePiece piece) {
    return piece.compareHypots(this.posnX, this.posnY, this.size);
  }

  // is this piece currently colliding with that piece?
  public boolean compareHypots(int posnX, int posnY, int size) {
    double maxHypot = Math.hypot(this.posnX - posnX, this.posnY - posnY);
    double trueHypot = this.size + size;
    return trueHypot >= maxHypot;
  }
}

// represents the game NBullets
class NBullets extends World {
  int bulletsLeft;
  int numShipsDestroyed;
  int tick;
  int screenWidth;
  int screenHeight;
  ILoGamePiece ships;
  ILoGamePiece bullets;

  NBullets(int bulletsLeft, int numShipsDestroyed, int screenWidth,
      int screenHeight, int tick, ILoGamePiece ships, ILoGamePiece bullets) {
    this.bulletsLeft = bulletsLeft;
    this.numShipsDestroyed = numShipsDestroyed;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.tick = tick;
    this.ships = ships;
    this.bullets = bullets;
  }

  NBullets(int bullets) {
    this(bullets, 0, 500, 300, 1, new MtLoGamePiece(), new MtLoGamePiece());
  }

  // draws the game, runs every tick
  @Override
  public WorldScene makeScene() {
    WorldScene game = new WorldScene(this.screenWidth, this.screenHeight);
    game = this.drawScoreboard(game);
    game = this.ships.placePieces(game);
    game = this.bullets.placePieces(game);
    return game;
  }

  // draws the scoreboard onto the game, runs every tick
  public WorldScene drawScoreboard(WorldScene game) {
    WorldImage txtImg = new TextImage("Score: " + this.numShipsDestroyed 
        + " Bullets left: " + this.bulletsLeft, 20, Color.RED);
    return game.placeImageXY(txtImg, this.screenWidth / 2, 15);
  }  

  // ends the game once all bullets are off the screen and no bullets are left
  public WorldEnd worldEnds() {
    if (this.bullets.len() == 0 && this.bulletsLeft == 0) {
      return new WorldEnd(true, this.lastScene(
          "Game over! Score: " + this.numShipsDestroyed));
    } else {
      return new WorldEnd(false, this.makeScene()); 
    }
  }

  //draws the final scene, called when the ends condition is hit
  @Override
  public WorldScene lastScene(String msg) {
    WorldImage txtImg = new TextImage(msg, 20, Color.RED);
    WorldScene game = new WorldScene(this.screenWidth, this.screenHeight);
    return game.placeImageXY(txtImg, this.screenWidth / 2, this.screenHeight / 2);
  }

  //handles keypresses. used for spacebar bullet shooting
  @Override
  public NBullets onKeyEvent(String key) {
    if (key.equals(" ") && this.bulletsLeft > 0) {
      return new NBullets(this.bulletsLeft - 1,
          this.numShipsDestroyed, 500, 300, this.tick + 1,
          this.ships,
          new ConsLoGamePiece(new Bullet(
              this.screenWidth / 2, this.screenHeight - 5, -10),
              this.bullets));
    }
    return this;
  }

  // handler for running tasks each tick
  @Override
  public NBullets onTick() {
    return this.spawnRandomShips()
        .updatePieces()
        .checkPiecesOOB()
        .checkOverlap()
        .upTick();
  }

  // returns new game state but with an updated score & updated ships/bullets if
  // any bullet(s) have collided with any ship(s)
  public NBullets checkOverlap() {
    int numNewShipsDestroyed = this.ships.len() 
        - this.ships.checkPieceOverlap(this.bullets).len();
    return new NBullets(this.bulletsLeft,
        this.numShipsDestroyed + numNewShipsDestroyed, 500, 300, this.tick,
        this.ships.checkPieceOverlap(this.bullets), this.bullets.checkPieceOverlap(this.ships));
  }

  // handler for spawning ships. every second, this spawns 
  // 1-3 ships on a random side of the game (left or right)
  public NBullets spawnRandomShips() {
    if (this.tick % 28 == 0) {
      int randAmt = new Random().nextInt(3);
      randAmt += 1;
      int leftOrRight = new Random().nextInt(2); // left = 0, right = 1
      return new NBullets(this.bulletsLeft,
          this.numShipsDestroyed, 500, 300, this.tick,
          this.spawnRandomShipsHelper(randAmt, leftOrRight),
          this.bullets);
    }
    return this;
  }

  // returns new list of the new ships to be added to the game 
  // after being given how many ships to be added & which side to add them on
  public ILoGamePiece spawnRandomShipsHelper(int randAmt, int leftOrRight) {
    if (randAmt == 0) {
      return this.ships;
    }
    if (leftOrRight == 0) {
      int spawnPos = (new Random().nextInt(
          this.screenHeight / 7 * 5) + this.screenHeight / 7);
      return new ConsLoGamePiece(
          new Ship(this.screenWidth, spawnPos, -3),
          spawnRandomShipsHelper(randAmt - 1, leftOrRight)); 
    }
    int spawnPos = (new Random().nextInt(
        this.screenHeight / 7 * 5) + this.screenHeight / 7);
    return new ConsLoGamePiece(
        new Ship(0, spawnPos, 3),
        spawnRandomShipsHelper(randAmt - 1, leftOrRight)); 
  }

  // increments the tick by 1, returns game state.
  public NBullets upTick() {
    return new NBullets(this.bulletsLeft,
        this.numShipsDestroyed, 500, 300, this.tick + 1,
        this.ships, this.bullets);
  }

  // returns new game state with the ships and bullets moved appropriately;
  // runs each tick
  public NBullets updatePieces() {
    return new NBullets(this.bulletsLeft, 
        this.numShipsDestroyed, 500, 300, this.tick,
        this.ships.updatePosns(), this.bullets.updatePosns());
  }

  // runs each tick, returning new game state which eliminates
  // all out-of-bounds pieces.
  public NBullets checkPiecesOOB() {
    return new NBullets(this.bulletsLeft, 
        this.numShipsDestroyed, this.screenWidth, this.screenHeight, this.tick,
        this.ships.checkOOB(this.screenHeight, this.screenWidth),
        this.bullets.checkOOB(this.screenHeight, this.screenWidth));
  }
}

// examples and tests for NBullets game.
class ExamplesNBullets {

  WorldScene game = new WorldScene(500, 300);

  ILoGamePiece mt = new MtLoGamePiece();

  AGamePiece bullet1 = new Bullet(10, 10, 5);
  AGamePiece bullet2 = new Bullet(100, 10, -5);
  AGamePiece bullet3 = new Bullet(80, 50, 0);
  AGamePiece bullet4 = new Bullet(10, 15, 5);
  AGamePiece bullet5 = new Bullet(100, 5, -5);
  AGamePiece bullet6 = new Bullet(80, 50, 0);
  AGamePiece bullet1Offspring1 = new Bullet(bullet1.posnX, bullet1.posnY, 10, 0, 2, 6);
  AGamePiece bullet1Offspring2 = new Bullet(bullet1.posnX, bullet1.posnY, -10, 0, 2, 6);

  ILoGamePiece b1OffspringList = new ConsLoGamePiece(bullet1Offspring1,
      new ConsLoGamePiece(bullet1Offspring2, mt));
  ILoGamePiece lob = new ConsLoGamePiece(bullet1,
      new ConsLoGamePiece(bullet2,
          new ConsLoGamePiece(bullet3, mt)));
  ILoGamePiece lob2 = new ConsLoGamePiece(bullet4,
      new ConsLoGamePiece(bullet5,
          new ConsLoGamePiece(bullet6, mt)));
  ILoGamePiece lobNoCol = b1OffspringList.mergeLists(
      new ConsLoGamePiece(bullet2,
          new ConsLoGamePiece(bullet3, mt)));
  ILoGamePiece lobUp = new ConsLoGamePiece(bullet1.updatePosn(),
      new ConsLoGamePiece(bullet2.updatePosn(),
          new ConsLoGamePiece(bullet3.updatePosn(), mt)));

  AGamePiece ship1 = new Ship(50, 50, 1);
  AGamePiece ship2 = new Ship(50, 40, -1);
  AGamePiece ship3 = new Ship(10, 12, 0);
  ILoGamePiece los = new ConsLoGamePiece(ship1,
      new ConsLoGamePiece(ship2,
          new ConsLoGamePiece(ship3, mt)));
  ILoGamePiece losNoCol = new ConsLoGamePiece(ship1,
      new ConsLoGamePiece(ship2, mt));
  ILoGamePiece losUp = new ConsLoGamePiece(ship1.updatePosn(),
      new ConsLoGamePiece(ship2.updatePosn(),
          new ConsLoGamePiece(ship3.updatePosn(), mt)));

  NBullets w1 = new NBullets(10);
  NBullets w2 = new NBullets(10, 0, 500, 300, 1, los, lob);
  NBullets w3 = new NBullets(10, 1, 500, 300, 1, losNoCol, lobNoCol);
  NBullets w4 = new NBullets(10, 1, 1, 1, 1, los, lob);

  boolean testBigBang(Tester t) {
    int worldWidth = 500;
    int worldHeight = 300;
    double tickRate = 1.0 / 28.0;
    return this.w1.bigBang(worldWidth, worldHeight, tickRate);
  }

  // BEGIN TESTERS FOR SHIP 

  //tester for Ship.updatePosn()
  boolean testShipUpdatePosn(Tester t) {
    return t.checkExpect(ship1.updatePosn(), new Ship(51, 50, 1))
        && t.checkExpect(ship2.updatePosn(), new Ship(49, 40, -1))
        && t.checkExpect(ship3.updatePosn(), ship3);
  }

  //tester for Ship.overlapsAny(ILoGamePiece bullets)
  boolean testShipOverlapsAny(Tester t) {
    return t.checkExpect(ship1.overlapsAny(lob), false)
        && t.checkExpect(ship3.overlapsAny(lob), true)
        && t.checkExpect(ship1.overlapsAny(mt), false);
  }

  //tester for Ship.handleAddNewBullets()
  boolean testShipHandleAddNewBullets(Tester t) {
    return t.checkExpect(ship1.handleAddNewBullets(mt), mt)
        && t.checkExpect(ship2.handleAddNewBullets(lob), lob);
  }

  //tester for Ship.handleCreateBulletList(ILoGamePiece, int)
  boolean testShipHandleCreateBulletList(Tester t) {
    return t.checkExpect(ship1.handleCreateBulletList(mt, 10), mt)
        && t.checkExpect(ship1.handleCreateBulletList(mt, 0), mt)
        && t.checkExpect(ship2.handleCreateBulletList(lob, 5), lob)
        && t.checkExpect(ship2.handleCreateBulletList(lob, 0), lob);
  }

  // BEGIN TESTERS FOR BULLET

  //tester for Bullet.updatePosn()
  boolean testBulletUpdatePosn(Tester t) {
    return t.checkExpect(bullet1.updatePosn(), new Bullet(10, 15, 5))
        && t.checkExpect(bullet2.updatePosn(), new Bullet(100, 5, -5))
        && t.checkExpect(bullet3.updatePosn(), bullet3)
        && t.checkExpect(new Bullet(10, 10, -5, 5, 1, 5).updatePosn(),
            new Bullet(5, 15, -5, 5, 1, 5));
  }

  //tester for Bullet.overlapsAny(ILoGamePiece ships)
  boolean testBulletOverlapsAny(Tester t) {
    return t.checkExpect(bullet1.overlapsAny(los), true)
        && t.checkExpect(bullet3.overlapsAny(los), false)
        && t.checkExpect(bullet1.overlapsAny(mt), false);
  }

  // FINISH THE BELOW SHIT LATER

  //tester for Bullet.handleAddNewBullets(ILoGamePiece)
  boolean testBulletHandleAddNewBullets(Tester t) {
    return t.checkExpect(bullet1.handleAddNewBullets(this.mt), this.b1OffspringList)
        && t.checkExpect(bullet2.handleAddNewBullets(this.lob),
            new ConsLoGamePiece(new Bullet(100,10,10,0,2,6), 
                new ConsLoGamePiece(new Bullet(100,10,-10,0,2,6), 
                    new ConsLoGamePiece(new Bullet(10,10,0,5,1,5),
                        new ConsLoGamePiece(new Bullet(100,10,0,-5,1,5), 
                            new ConsLoGamePiece(new Bullet(80,50,0,0,1,5), mt))))));
  }

  //tester for Bullet.handleCreateBulletList(ILoGamePiece, int)
  boolean testBulletHandleCreateBulletList(Tester t) {
    return t.checkExpect(bullet1.handleCreateBulletList(mt, 2), this.b1OffspringList)
        && t.checkExpect(bullet1.handleCreateBulletList(mt, 0), mt)
        && t.checkExpect(bullet2.handleCreateBulletList(lob, 5), 
            new ConsLoGamePiece(new Bullet(100,10,-10,0,2,6), 
                new ConsLoGamePiece(new Bullet(100,10,10,0,2,6), 
                    new ConsLoGamePiece(new Bullet(100,10,-10,0,2,6),
                        new ConsLoGamePiece(new Bullet(100,10,10,0,2,6), 
                            new ConsLoGamePiece(new Bullet(100,10,-10,0,2,6), mt))))))
        && t.checkExpect(bullet2.handleCreateBulletList(lob, 0), mt);
  }

  // BEGIN TESTERS FOR NBULLETS

  //tester for NBullets.makeScene(WorldScene)
  boolean testMakeScene(Tester t) {
    return t.checkExpect(w1.makeScene(), this.game.placeImageXY(
        new TextImage("Score: 0 Bullets left: 10", 20, Color.RED), 250, 15));
  }

  //tester for nbullets.worldends
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(new NBullets(0).worldEnds(), new WorldEnd(true, new NBullets(0).lastScene(
        "Game over! Score: 0")))
        && t.checkExpect(w1.worldEnds(), new WorldEnd(false, this.game.placeImageXY(
            new TextImage("Score: 0 Bullets left: 10", 20, Color.RED), 250, 15)));
  }

  //tester for nbullets.lastScene
  boolean testLastScene(Tester t) {
    return t.checkExpect(w1.lastScene("Game over! Score: 0"), game.placeImageXY(
        new TextImage("Game over! Score: 0", 20, Color.RED), 
        250, 150)) 
        && t.checkExpect(w1.lastScene("Game over! Score: 20"), game.placeImageXY(
            new TextImage("Game over! Score: 20", 20, Color.RED), 
            250, 150));
  }

  //tester for nbullets.onKeyEvent(String)
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(w1.onKeyEvent(" "), new NBullets(9,
        0, 500, 300, 2,
        new MtLoGamePiece(),
        new ConsLoGamePiece(new Bullet(250, 295, -10), this.mt)))
        && t.checkExpect(w1.onKeyEvent("q"), w1);
  }

  // tester for NBUllets.checkOverlap()
  boolean testCheckOverlap(Tester t) {
    return t.checkExpect(new NBullets(10).checkOverlap(), new NBullets(10))
        && t.checkExpect(w2.checkOverlap(), w3);
  }

  // tester for NBullets.upTick()
  boolean testUpTick(Tester t) {
    return t.checkExpect(w2.upTick(), new NBullets(10, 0, 500, 300, 2, los, lob))
        && t.checkExpect(w3.upTick(), new NBullets(10, 1, 500, 300, 2, losNoCol, lobNoCol));
  }

  //tester for NBullets.updatePieces()
  boolean testUpdatePieces(Tester t) {
    return t.checkExpect(new NBullets(10).updatePieces(), new NBullets(10))
        && t.checkExpect(this.w2.updatePieces(), new NBullets(10, 0, 500, 300, 1, losUp, lobUp));
  }

  // tester for NBullets.checkPiecesOOB()
  boolean testcheckPiecesOOB(Tester t) {
    return t.checkExpect(new NBullets(10).checkPiecesOOB(), new NBullets(10)) 
        && t.checkExpect(this.w2.checkPiecesOOB(), this.w2)
        && t.checkExpect(this.w4.checkPiecesOOB(), new NBullets(10, 1, 1, 1, 1, mt, mt));
  }

  // BEGIN TESTERS FOR AGAMEPIECE

  //tester for AGamePiece.compareHypots(int posnX, int posnY, int size)
  boolean testCompareHypots(Tester t) {
    return t.checkExpect(ship1.compareHypots(1000, 1000, 1), false)
        && t.checkExpect(ship2.compareHypots(50, 50, 10), true)
        && t.checkExpect(bullet1.compareHypots(1000, 1000, 10), false)
        && t.checkExpect(bullet2.compareHypots(100, 10, 5), true);
  }

  //tester for AGamePiece.draw()
  boolean testDraw(Tester t) {
    return t.checkExpect(bullet1.draw(), new CircleImage(5, OutlineMode.SOLID, Color.pink))
        && t.checkExpect(bullet2.draw(), new CircleImage(5, OutlineMode.SOLID, Color.pink))
        && t.checkExpect(new Bullet(10, 10, -5, 5, 1, 10).draw(),
            new CircleImage(10, OutlineMode.SOLID, Color.pink))
        && t.checkExpect(ship1.draw(), new CircleImage(10, OutlineMode.SOLID, Color.blue))
        && t.checkExpect(ship2.draw(), new CircleImage(10, OutlineMode.SOLID, Color.blue));
  }

  //tester for AGamePiece.isOOB(int height, int width)
  boolean testIsOOB(Tester t) {
    return t.checkExpect(ship1.isOOB(1, 1), true)
        && t.checkExpect(ship2.isOOB(300, 500), false)
        && t.checkExpect(bullet1.isOOB(1, 1), true)
        && t.checkExpect(bullet3.isOOB(300, 500), false);
  }

  //tester for AGamePiece.place(WorldScene)
  boolean testPlace(Tester t) {
    return t.checkExpect(ship1.place(game), 
        game.placeImageXY(
            new CircleImage(10, OutlineMode.SOLID, Color.blue),
            ship1.posnX, ship1.posnY))
        && t.checkExpect(bullet1.place(game), 
            game.placeImageXY(
                new CircleImage(5, OutlineMode.SOLID, Color.pink),
                bullet1.posnX, bullet1.posnY));
  }

  //tester for AGamePiece.overlapsHelper(IGamePiece piece) 
  boolean testOverlapsHelper(Tester t) {
    return t.checkExpect(bullet1.overlapsHelper(ship2), false)
        && t.checkExpect(bullet3.overlapsHelper(ship2), false)
        && t.checkExpect(ship3.overlapsHelper(bullet1), true);
  }

  // BEGIN TESTS FOR ILOGAMEPIECE

  //tester for ILoGamePieces.overlaps
  boolean testOverlaps(Tester t) {
    return t.checkExpect(lob.overlaps(ship2), false)
        && t.checkExpect(mt.overlaps(ship1), false)
        && t.checkExpect(lob.overlaps(ship3), true);
  }

  // test for ILoGamePiece.updatePosns()
  boolean testUpdatePosns(Tester t) {
    return t.checkExpect(mt.updatePosns(), mt)
        && t.checkExpect(lob.updatePosns(), lob2);
  }

  //test for ILoGamePiece.checkOOB(int,int)
  boolean testCheckOOB(Tester t) {
    return t.checkExpect(lob.checkOOB(500, 500), lob)
        && t.checkExpect(mt.checkOOB(500, 500), mt)
        && t.checkExpect(los.checkOOB(11,11), 
            new ConsLoGamePiece(new Ship(10,12,0), mt));
  }

  // adds pieces for explosion effect
  //ILoGamePiece addNewBullets(IGamePiece firstBullet);

  //tester for ILoGamePiece.addNewBullets(IGamePiece) 
  boolean testAddNewBullets(Tester t) {
    return t.checkExpect(mt.addNewBullets(bullet1),  
        new ConsLoGamePiece(new Bullet(10,10,10,0,2,6),
            new ConsLoGamePiece(new Bullet(10,10,-10,0,2,6), mt)))
        && t.checkExpect(lob.addNewBullets(bullet1), 
            new ConsLoGamePiece(new Bullet(10,10,10,0,2,6), 
                new ConsLoGamePiece(new Bullet(10,10,-10,0,2,6), 
                    new ConsLoGamePiece(new Bullet(10,10,0,5,1,5),
                        new ConsLoGamePiece(new Bullet(100,10,0,-5,1,5), 
                            new ConsLoGamePiece(new Bullet(80,50,0,0,1,5), mt))))));
  }

  //tester for ILoGamePiece.createBulletList(IGamePiece, int) 
  boolean testCreateBulletList(Tester t) {
    return t.checkExpect(lob.createBulletList(bullet1, 2), 
        new ConsLoGamePiece(new Bullet(10,10,10,0,2,6), 
            new ConsLoGamePiece(new Bullet(10,10,-10,0,2,6), mt)))
        && t.checkExpect(lob.createBulletList(bullet1, 0), mt)
        && t.checkExpect(mt.createBulletList(bullet1, 0), mt);
  }

  //test for ILoGamePiece.mergeLists(ILoGamePiece) 
  boolean testMergeLists(Tester t) {
    return t.checkExpect(mt.mergeLists(mt), mt)
        && t.checkExpect(mt.mergeLists(b1OffspringList), b1OffspringList)
        && t.checkExpect(b1OffspringList.mergeLists(mt), b1OffspringList)
        && t.checkExpect(new ConsLoGamePiece(new Bullet(0, 0, 1),
            new ConsLoGamePiece(new Bullet(1,1,1), mt)).mergeLists(
                new ConsLoGamePiece(
                    new Bullet(1,2,3), mt)), new ConsLoGamePiece(
                        new Bullet(0, 0, 1),
                        new ConsLoGamePiece(new Bullet(1,1,1), 
                            new ConsLoGamePiece(
                                new Bullet(1,2,3), mt))));
  }

  //test for ILoGamePiece.len()
  boolean testLen(Tester t) {
    return t.checkExpect(mt.len(), 0) 
        && t.checkExpect(los.len(), 3);
  }

}