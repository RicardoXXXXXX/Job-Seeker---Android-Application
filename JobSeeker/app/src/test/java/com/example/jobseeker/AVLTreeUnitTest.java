package com.example.jobseeker;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * This class defines unit tests for the AVLTree data structure.
 */
public class AVLTreeUnitTest {

    private AVLTree avlTree;
    private JSONObject job1, job2, job3, job4;

    /**
     * Setup method that initializes the AVLTree and JSON objects before each test.
     */
    @Before
    public void setUp() {
        avlTree = AVLTree.getInstance();
        job1 = new JSONObject();
        job2 = new JSONObject();
        job3 = new JSONObject();
        job4 = new JSONObject();
    }

    /**
     * Test to ensure that the AVLTree follows the singleton pattern behavior.
     */
    @Test
    public void testSingletonBehavior() {
        AVLTree anotherInstance = AVLTree.getInstance();
        assertEquals(avlTree, anotherInstance);
    }

    /**
     * Test insertion and search operations in the AVLTree.
     */
    @Test
    public void testInsertionAndSearch() {
        avlTree.insert("California", job1);
        avlTree.insert("Texas", job2);
        avlTree.insert("New York", job3);

        List<JSONObject> caliJobs = avlTree.search("California");
        assertTrue(caliJobs.contains(job1));

        List<JSONObject> texasJobs = avlTree.search("Texas");
        assertTrue(texasJobs.contains(job2));

        List<JSONObject> newYorkJobs = avlTree.search("New York");
        assertTrue(newYorkJobs.contains(job3));
    }

    /**
     * Test insertion of multiple jobs with the same location in the AVLTree.
     */
    @Test
    public void testInsertionOfSameLocationDifferentJobs() {
        avlTree.insert("California", job1);
        avlTree.insert("California", job4);

        List<JSONObject> caliJobs = avlTree.search("California");
        assertTrue(caliJobs.contains(job1));
        assertTrue(caliJobs.contains(job4));
    }

    /**
     * Test searching for a non-existent location in the AVLTree.
     */
    @Test
    public void testSearchForNonExistentLocation() {
        assertNull(avlTree.search("Paris"));
    }
}
