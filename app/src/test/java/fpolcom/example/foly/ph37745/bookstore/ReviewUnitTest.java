package fpolcom.example.foly.ph37745.bookstore;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import an.ph69924.bansach.models.Review;

public class ReviewUnitTest {

    @Test
    public void testReviewModel() {
        Review review = new Review("user123", "User Name", "book456", 4.5f, "Great book!");
        
        assertEquals("user123", review.getUserId());
        assertEquals("User Name", review.getUserName());
        assertEquals("book456", review.getBookId());
        assertEquals(4.5f, review.getRating(), 0.0f);
        assertEquals("Great book!", review.getComment());
    }

    @Test
    public void testAverageRatingCalculation() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("u1", "n1", "b1", 5, "c1"));
        reviews.add(new Review("u2", "n2", "b1", 4, "c2"));
        reviews.add(new Review("u3", "n3", "b1", 3, "c3"));

        float total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        float avg = total / reviews.size();

        assertEquals(4.0f, avg, 0.0f);
    }
}
