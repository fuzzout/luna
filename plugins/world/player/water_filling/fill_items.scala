/*
 Adds functionality for filling items with water.

 SUPPORTS:
  -> All fillable items.
  -> A variety of water sources.
*/

import io.luna.game.action.{Action, ProducingAction}
import io.luna.game.event.impl.ItemOnObjectEvent
import io.luna.game.model.item.Item
import io.luna.game.model.mob.{Animation, Mob, Player}


/* Filling animation. */
private val ANIMATION = new Animation(832)

/* Identifiers of various water sources. */
private val WATER_SOURCES = Set(153, 879, 880, 34579, 2864, 6232, 878, 884, 3359, 3485, 4004, 4005,
  5086, 6097, 8747, 8927, 9090, 6827, 3460)

/* Items that can be filled with water. */
private val FILLABLES = Map(
  1923 -> 1921, // Bowl
  229 -> 227, // Vial
  1925 -> 1929, // Bucket
  1980 -> 4458, // Cup
  1935 -> 1937 // Jug
)


/* Fills all items in an inventory with water. */
private final class FillAction(val evt: ItemOnObjectEvent, val oldId: Int,
                               val newId: Int) extends ProducingAction(evt.plr, true, 2) {
  override def remove = Array(new Item(oldId))

  override def add = Array(new Item(newId))

  override def onProduce() = mob.animation(ANIMATION)

  override def isEqual(other: Action[_]) = {
    other match {
      case action: FillAction if
      evt.objectId == action.evt.objectId &&
        oldId == action.oldId &&
        newId == action.newId => true

      case _ => false
    }
  }
}


/* Intercept an item on object event, fill items if applicable. */
on[ItemOnObjectEvent] { msg =>
  if (WATER_SOURCES.contains(msg.objectId)) {

    FILLABLES.get(msg.itemId).foreach { fillable =>
      msg.plr.submitAction(new FillAction(msg, msg.itemId, fillable))
      msg.terminate
    }
  }
}