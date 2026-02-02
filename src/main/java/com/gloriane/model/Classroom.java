package com.gloriane.model;

import java.util.HashSet;
import java.util.Set;

public class Classroom {
    private int id;
    private String name;
    private int capacity;
    private Set<String> equipment;
    private boolean isAccessible;

    // ===== CONSTRUCTOR =====
    // Registering into the database
    public Classroom(String name, int capacity, Set<String> equipment, boolean isAccessible) {
        this.name = name;
        this.capacity = capacity;
        this.equipment = new HashSet<>(equipment);  // We create a NEW HashSet to avoid sharing the same equipment list with other objects
        this.isAccessible = isAccessible;
    }

    // Constructor for loading an EXISTING classroom from database
    public Classroom() {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.equipment = new HashSet<>(equipment);
        this.isAccessible = isAccessible;
    }



    // ===== GETTERS AND SETTERS =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Gets the equipment set. We return a NEW set to prevent outside code from modifying our internal set.This is called DEFENSIVE COPYING - protecting our data!
    public Set<String> getEquipment() {
        return new HashSet<>(equipment);
    }

    public void setEquipment(Set<String> equipment) {
        this.equipment = new HashSet<>(equipment);
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    // ===== HELPER METHODS =====
    // These methods provide business logic functionality for classroom operations

    // Adds a piece of equipment to this classroom. For example: addEquipment("Projector")
    public void addEquipment(String equipmentItem) {
        this.equipment.add(equipmentItem);
    }

    // Helper method: Removes a piece of equipment from this classroom
    public void removeEquipment(String equipmentItem) {
        this.equipment.remove(equipmentItem);
    }

    // Helper method: Checks if this classroom has a specific piece of equipment
    public boolean hasEquipment(String equipmentItem) {
        return this.equipment.contains(equipmentItem);
    }

    /**
     * Helper method: Checks if this classroom has ALL the required equipment
     * Example: If someone needs ["Projector", "Whiteboard"], this method checks if our room has BOTH of those items.
     * Set of equipment that is needed
     * room has all required equipment, false otherwise
     */
    // This checks if our equipment set contains ALL items in requiredEquipment
    public boolean hasAllEquipment(Set<String> requiredEquipment) {
        return this.equipment.containsAll(requiredEquipment);
    }


    // Check capacity: our capacity must be >= what they need
    public boolean meetsRequirements(int minCapacity, Set<String> requiredEquipment, boolean needsAccessibility) {
        boolean capacityOk = this.capacity >= minCapacity;

        // Check equipment: we must have all the equipment they need
        boolean equipmentOk = (requiredEquipment == null || requiredEquipment.isEmpty())
                || this.hasAllEquipment(requiredEquipment);

        // Check accessibility: if they need accessibility, we must have it
        boolean accessibilityOk = !needsAccessibility || this.isAccessible;

        // ALL conditions must be true
        return capacityOk && equipmentOk && accessibilityOk;
    }

    @Override
    public String toString() {
        return String.format("Classroom[id=%d, name=%s, capacity=%d, equipment=%s, accessible=%s]",
                id, name, capacity, equipment, isAccessible ? "Yes" : "No");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Classroom classroom = (Classroom) obj;
        return id == classroom.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
