package warehouse;

/*
 *
 * This class implements a warehouse on a Hash Table like structure, 
 * where each entry of the table stores a priority queue. 
 * Due to your limited space, you are unable to simply rehash to get more space. 
 * However, you can use your priority queue structure to delete less popular items 
 * and keep the space constant.
 * 
 * @author Ishaan Ivaturi
 */ 
public class Warehouse {
    private Sector[] sectors;
    
    // Initializes every sector to an empty sector
    public Warehouse() {
        sectors = new Sector[10];

        for (int i = 0; i < 10; i++) {
            sectors[i] = new Sector();
        }
    }
    
    /**
     * Provided method, code the parts to add their behavior
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    public void addProduct(int id, String name, int stock, int day, int demand) {
        evictIfNeeded(id);
        addToEnd(id, name, stock, day, demand);
        fixHeap(id);
    }

    /**
     * Add a new product to the end of the correct sector
     * Requires proper use of the .add() method in the Sector class
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    private void addToEnd(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD
        Product p = new Product(id, name, stock, day, demand); // take in the arguments and create a Product object
        // The sector index you add to should be the last digit of the given product id.
        int sectorIndex = id % 10;
        // The initial date of last purchase of your new Product should be set to the current day (which is passed in).
        p.setLastPurchaseDay(day);
        Sector currSector = sectors[sectorIndex]; // to make things easier to read
        currSector.add(p); // add this product to the correct sector
    }

    /**
     * Fix the heap structure of the sector, assuming the item was already added
     * Requires proper use of the .swim() and .getSize() methods in the Sector class
     * @param id The id of the item which was added
     */
    private void fixHeap(int id) {
        // IMPLEMENT THIS METHOD
        int sectorIndex = id % 10;
        Sector currSector = sectors[sectorIndex];
        for(int i = 2; i <= currSector.getSize(); i++){
            currSector.swim(i);
        }
    }

    /**
     * Delete the least popular item in the correct sector, only if its size is 5 while maintaining heap
     * Requires proper use of the .swap(), .deleteLast(), and .sink() methods in the Sector class
     * @param id The id of the item which is about to be added
     */
    private void evictIfNeeded(int id) {
       // IMPLEMENT THIS METHOD
       int sectorIndex = id % 10;
       Sector currSector = sectors[sectorIndex];
       
       if(currSector.getSize() == 5){ // if the current sector is at full capacity (5), delete the value at the last index
        currSector.swap(1,5);
        currSector.deleteLast();
        currSector.sink(1);
       }

    }

    /**
     * Update the stock of some item by some amount
     * Requires proper use of the .getSize() and .get() methods in the Sector class
     * Requires proper use of the .updateStock() method in the Product class
     * @param id The id of the item to restock
     * @param amount The amount by which to update the stock
     */
    public void restockProduct(int id, int amount) {
        // IMPLEMENT THIS METHOD

        int sectorIndex = id % 10;
        Sector currSector = sectors[sectorIndex];
        for(int i = 1; i <= currSector.getSize(); i++){
            if(currSector.get(i) != null){ 
                if(currSector.get(i).getId() == id){
                    currSector.get(i).updateStock(amount);
                }
            }
        }
    }
    
    /**
     * Delete some arbitrary product while maintaining the heap structure in O(logn)
     * Requires proper use of the .getSize(), .get(), .swap(), .deleteLast(), .sink() and/or .swim() methods
     * Requires proper use of the .getId() method from the Product class
     * @param id The id of the product to delete
     */
    public void deleteProduct(int id) {
        // IMPLEMENT THIS METHOD
        int sectorIndex = id % 10;
        Sector currSector = sectors[sectorIndex];
        for(int i = 1; i <= currSector.getSize(); i++){
            if(currSector.get(i) != null){
                if(currSector.get(i).getId() == id){
                    currSector.swap(i, currSector.getSize());
                    currSector.deleteLast();
                    currSector.sink(1);
                }
            }
        }
    }
    
    /**
     * Simulate a purchase order for some product
     * Requires proper use of the getSize(), sink(), get() methods in the Sector class
     * Requires proper use of the getId(), getStock(), setLastPurchaseDay(), updateStock(), updateDemand() methods
     * @param id The id of the purchased product
     * @param day The current day
     * @param amount The amount purchased
     */
    public void purchaseProduct(int id, int day, int amount) {
        // IMPLEMENT THIS METHOD
        int sectorId = id % 10;
        Sector currSector = sectors[sectorId];
        for(int i = 1; i <= currSector.getSize(); i++){
            if(currSector.get(i) != null){
                Product currProduct = currSector.get(i);
                if(amount > currProduct.getStock()){
                    return;
                }
                if(currProduct.getId() == id){
                    currProduct.setLastPurchaseDay(day);
                    currProduct.setStock(currProduct.getStock() - amount);
                    currProduct.updateDemand(amount);
                    currSector.sink(i);
                }
            }
        }

    }
    
    /**
     * Construct a better scheme to add a product, where empty spaces are always filled
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    public void betterAddProduct(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD
        int sectorId = id % 10;
        Sector initialSector = sectors[sectorId];
        if((initialSector.get(5) == null)){
            addProduct(id, name, stock, day, demand);
        } else { // initial sector full -- look through sectors[] and find an empty sector
            int counter = sectorId + 1;
            if(sectorId == 9){
                counter = 0;
            }
            while(counter != sectorId && (sectors[counter].get(5) != null)){ // while we do not return to initial sector AND while are still at a sector that is full
                if(counter == 9){
                    counter = 0;
                } else {
                    counter++;
                }
            } // once we exit this loop, we are either at the sector where we started OR at a different sector that is empty
            if(counter == sectorId){
                addProduct(id, name, stock, day, demand);
            }
            if(sectors[counter].get(5) == null){ // we found a NOT full sector
                Product p = new Product(id, name, stock, day, demand);
                p.setLastPurchaseDay(day);
                sectors[counter].add(p);
                fixHeap(id);
            }
        }
    }

    /*
     * Returns the string representation of the warehouse
     */
    public String toString() {
        String warehouseString = "[\n";

        for (int i = 0; i < 10; i++) {
            warehouseString += "\t" + sectors[i].toString() + "\n";
        }
        
        return warehouseString + "]";
    }

    /*
     * Do not remove this method, it is used by Autolab
     */ 
    public Sector[] getSectors () {
        return sectors;
    }
}