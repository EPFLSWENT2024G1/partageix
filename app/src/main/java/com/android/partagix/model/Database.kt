package com.android.partagix.model

import android.location.Location
import android.util.Log
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Date
import java.util.concurrent.CountDownLatch

class Database(database: FirebaseFirestore = Firebase.firestore) {

  private val db = database
  private val users = db.collection("users")
  private val items = db.collection("items")
  private val loan = db.collection("loan")
  private val categories = db.collection("categories")
  private val itemLoan = db.collection("item_loan")

  init {
    // createExampleForDb()
  }

  fun getUser(idUser: String, onSuccess: (User) -> Unit) {
    users
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.data["id"] as String == idUser) {

              getUserInventory(idUser) { inventory ->
                val user =
                    User(
                        document.data["id"] as String,
                        document.data["name"] as String,
                        document.data["addr"] as String,
                        document.data["rank"] as String,
                        inventory)
                onSuccess(user)
              }
            }
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting user", it) }
  }

  fun getItems(onSuccess: (List<Item>) -> Unit) {
    items
        .get()
        .addOnSuccessListener { result ->
          categories
              .get()
              .addOnSuccessListener { result2 ->
                val categories = mutableMapOf<String, Category>()
                for (document in result2) {
                  categories[document.data["id"] as String] =
                      Category(document.data["id"] as String, document.data["name"] as String)
                }

                val ret = mutableListOf<Item>()
                for (document in result) {
                  val locationMap = document.data["location"] as HashMap<*, *>
                  val location = toLocation(locationMap)

                  val visibility = (document.data["visibility"] as Long).toInt()

                  val item =
                      Item(
                          document.data["id"] as String,
                          categories[document.data["id_category"] as String]!!,
                          document.data["name"] as String,
                          document.data["description"] as String,
                          Visibility.values()[visibility],
                          document.data["quantity"] as Long,
                          location,
                          document.data["id_user"] as String,
                      )
                  ret.add(item)
                }
                onSuccess(ret)
              }
              .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting items", it) }
  }

  private fun toLocation(locationMap: HashMap<*, *>): Location {
    val latitude = locationMap["latitude"] as Double
    val longitude = locationMap["longitude"] as Double

    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude
    return location
  }

  private fun locationToMap(location: Location): Map<String, Any?> {
    val locationMap = mutableMapOf<String, Any?>()
    locationMap["latitude"] = location.latitude
    locationMap["longitude"] = location.longitude
    // Add other relevant properties if needed

    return locationMap
  }

  fun getUserInventory(userId: String, onSuccess: (Inventory) -> Unit) {
    getItems { items ->
      val listItems = mutableListOf<Item>()
      for (item in items) {
        if (item.idUser == userId) {
          listItems.add(item)
        }
      }
      onSuccess(Inventory(userId, listItems))
    }
  }

  fun getLoans(onSuccess: (List<Loan>) -> Unit) {
    loan
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Loan>()
          for (document in result) {
            val start_date: Timestamp = document.data["start_date"] as Timestamp
            val end_date: Timestamp = document.data["end_date"] as Timestamp

            val loan =
                Loan(
                    document.data["id_owner"] as String,
                    document.data["id_loaner"] as String,
                    document.data["id_item"] as String,
                    start_date.toDate(),
                    end_date.toDate(),
                    document.data["review_owner"] as String,
                    document.data["review_loaner"] as String,
                    document.data["comment_owner"] as String,
                    document.data["comment_loaner"] as String,
                    LoanState.FINISHED)
            ret.add(loan)
          }
          onSuccess(ret)
        }
        .addOnFailureListener { Log.e(TAG, "Error getting loans", it) }
  }

  fun getCategories(onSuccess: (List<Category>) -> Unit) {
    categories
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Category>()
          for (document in result) {
            val category =
                Category(
                    document.data["id"] as String,
                    document.data["name"] as String,
                )
            ret.add(category)
          }
          onSuccess(ret)
        }
        .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
  }

  private fun getNewUid(collection: CollectionReference): String {
    val uidDocument = collection.document()
    return uidDocument.id
  }

  private fun createExampleForDb(
      users: CollectionReference = this.users,
      items: CollectionReference = this.items,
      loan: CollectionReference = this.loan,
      categories: CollectionReference = this.categories
  ) {
    val idUser = getNewUid(users)
    val data1 =
        hashMapOf(
            "id" to idUser,
            "name" to "name",
            "addr" to "addr",
            "rank" to "rank",
        )
    users.document(idUser).set(data1)

    val idCategory = getNewUid(categories)
    val data2 =
        hashMapOf(
            "id" to idCategory,
            "name" to "name",
        )
    categories.document(idCategory).set(data2)

    val idItem = getNewUid(items)

    val data3 =
        hashMapOf(
            "id" to idItem,
            "id_category" to idCategory,
            "name" to "name",
            "description" to "description",
            "visibility" to 0,
            "quantity" to 1,
            "location" to locationToMap(Location("")),
            "id_user" to idUser,
        )
    items.document(idItem).set(data3)

    val idLoan = getNewUid(loan)
    val data5 =
        hashMapOf(
            "id_owner" to idUser,
            "id_loaner" to idUser,
            "id_item" to idItem,
            "start_date" to Date(),
            "end_date" to Date(),
            "review_owner" to "Review",
            "review_loaner" to "Review",
            "comment_owner" to "Comment",
            "comment_loaner" to "Comment",
            "state" to LoanState.FINISHED.toString())
    loan.document(idLoan).set(data5)

    val idItemLoan = getNewUid(itemLoan)
    val data6 = hashMapOf("id_item" to idItem, "id_loan" to idLoan)
    itemLoan.document(idItemLoan).set(data6)
  }

  private fun resetDB() {
    val latch = CountDownLatch(6)

    users.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    items.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }

    loan.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    categories.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    itemLoan.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }

    latch.await()
  }

  fun createItem(userId: String, newItem: Item) {

    val idItem = getNewUid(items)
    val data3 =
        hashMapOf(
            "id" to idItem,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "id_user" to userId,
            "visibility" to newItem.visibility.ordinal,
            "quantity" to newItem.quantity,
            "location" to locationToMap(newItem.location))
    items.document(idItem).set(data3)
  }

  fun setItem(newItem: Item) {
    val data3 =
        hashMapOf(
            "id" to newItem.id,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "id_user" to newItem.idUser,
            "visibility" to newItem.visibility.ordinal,
            "quantity" to newItem.quantity,
            "location" to locationToMap(newItem.location),
        )
    items.document(newItem.id).set(data3)
  }

  fun getItem(id: String, onSuccess: (Item) -> Unit) {
    getItems { items ->
      for (item in items) {
        if (item.id == id) {
          onSuccess(item)
        }
      }
    }
  }

  /**
   * Get the category id from a category name
   *
   * @param nameCategory the name of the category
   * @param onSuccess the function to call when the id is found
   */
  fun getIdCategory(nameCategory: String, onSuccess: (String) -> Unit) {
    categories
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if ((document.data["name"] as String).equals(nameCategory, ignoreCase = true)) {
              onSuccess(document.data["id"] as String)
            }
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting idCategory", it) }
  }

  companion object {
    private const val TAG = "Database"
  }
}
