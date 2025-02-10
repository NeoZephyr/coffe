package algorithm.leetcode.queue;

import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

public class PetQueue {

    Queue<StampedPet> dogQueue;
    Queue<StampedPet> catQueue;
    int count;

    public PetQueue() {
        this.dogQueue = new LinkedList<>();
        this.catQueue = new LinkedList<>();
        this.count = 0;
    }

    public void add(Pet pet) {
        if (pet.type.equals("dog")) {
            dogQueue.add(new StampedPet(pet, count++));
        } else if (pet.type.equals("cat")) {
            catQueue.add(new StampedPet(pet, count++));
        } else {
            throw new RuntimeException("error, not dog or cat");
        }
    }

    public Pet poll() {
        if (hasDog() && hasCat()) {
            if (dogQueue.peek().getCount() < catQueue.peek().getCount()) {
                return dogQueue.poll().pet;
            } else {
                return catQueue.poll().pet;
            }
        } else if (hasDog()) {
            return dogQueue.poll().pet;
        } else if (hasCat()) {
            return catQueue.poll().pet;
        } else {
            throw new RuntimeException("error, queue is empty");
        }
    }

    public Dog pollDog() {
        if (hasDog()) {
            return (Dog) dogQueue.poll().pet;
        } else {
            throw new RuntimeException("error, queue is empty");
        }
    }

    public Cat pollCat() {
        if (hasCat()) {
            return (Cat) catQueue.poll().pet;
        } else {
            throw new RuntimeException("error, queue is empty");
        }
    }

    public boolean hasPet() {
        return !catQueue.isEmpty() || !dogQueue.isEmpty();
    }

    public boolean hasDog() {
        return !dogQueue.isEmpty();
    }

    public boolean hasCat() {
        return !catQueue.isEmpty();
    }

    @Getter
    static class Pet {
        String type;

        public Pet(String type) {
            this.type = type;
        }
    }

    static class Dog extends Pet {
        public Dog() {
            super("dog");
        }
    }

    static class Cat extends Pet {

        public Cat() {
            super("cat");
        }
    }

    @Getter
    static class StampedPet {
        Pet pet;
        long count;

        public StampedPet(Pet pet, long count) {
            this.pet = pet;
            this.count = count;
        }

        public String getType() {
            return pet.getType();
        }
    }
}
