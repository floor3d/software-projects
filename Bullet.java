import java.awt.Color;

// represents a bullet in the game
class Bullet extends AGamePiece {
  int tier = 1;
  
  Bullet(int posnX, int posnY, int velocityX, int velocityY, int tier, int size) {
    super(posnX, posnY, velocityX, velocityY, size, Color.pink);
    this.tier = tier;
  }
  
  Bullet(int posnX, int posnY, int velocityY) {
    this(posnX, posnY, 0, velocityY, 1, 5);
  }

  //runs each tick to make the bullet move
  public IGamePiece updatePosn() {
    return new Bullet(this.posnX + this.velocityX, 
        this.posnY + this.velocityY, this.velocityX, this.velocityY,
        this.tier, this.size);
  }

  // checks if this bullet overlaps with any of the ships
  public boolean overlapsAny(ILoGamePiece ships) {
    return ships.overlaps(this);
  }

  // returns list of bullets, adding extra bullets after collision
  public ILoGamePiece handleAddNewBullets(ILoGamePiece lst) {
    ILoGamePiece addToList = lst.createBulletList(this, this.tier + 1);
    return addToList.mergeLists(lst);
  }
  

  // creates list of bullets to be added (explosion) to existing bullet list
  public ILoGamePiece handleCreateBulletList(ILoGamePiece ilgp, int i) {
    int origTier = this.tier;
    if (i == 0) {
      return new MtLoGamePiece();
    }
    else if (i > 6) {
      i = 6;
      origTier = 6;
    }
    int degreeCalc = i * 360 / (origTier + 1);
    int velX = (int) (10 * Math.cos(Math.toRadians(degreeCalc)));
    int velY = (int) (10 * Math.sin(Math.toRadians(degreeCalc)));
    return new ConsLoGamePiece(
        new Bullet(this.posnX, this.posnY, velX, velY, origTier + 1, this.size + 1),
        ilgp.createBulletList(this, i - 1));
  }
}