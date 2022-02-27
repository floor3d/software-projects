import java.awt.Color;

// represents a ship in the game 
class Ship extends AGamePiece {  
  Ship(int posnX, int posnY, int velocityX) {
    super(posnX, posnY, velocityX, 0, 10, Color.blue);
  }

  //runs each tick to move the ship along
  public IGamePiece updatePosn() {
    return new Ship(this.posnX + this.velocityX, this.posnY, this.velocityX);
  }


  // does this ship overlap with any of those bullets?
  public boolean overlapsAny(ILoGamePiece bullets) {
    return bullets.overlaps(this);
  }

  // returns current list since ships should not be adding
  // any new bullets. 
  // Bullet functionality: 
  // "" returns list of bullets, adding extra bullets after collision ""
  public ILoGamePiece handleAddNewBullets(ILoGamePiece lst) {
    return lst;
  }


  // returns current list since ships should not be creating 
  // lists of bullets.
  // Bullet functionality: 
  // "" creates list of bullets to be added (explosion) to existing bullet list ""
  public ILoGamePiece handleCreateBulletList(ILoGamePiece that, int i) {
    return that;
  }
}