
#include <gtest/gtest.h>

#include <RingBuffer/ring_buffer.h>
#include <RingBuffer/ring_buffer_extra.h>
#include <Jsmn/jsmn.h>

TEST(RingBuffer, CreateCorrectCapacity)
{
    const size_t capacity = 150;
    ring_buffer_t *rb = create_ring_buffer(capacity);
    ASSERT_TRUE(rb != NULL);
    ASSERT_EQ(rb->capacity, capacity);
}

TEST(RingBuffer, PushAndPopData_Basic)
{
    const size_t capacity = 150;
    ring_buffer_t *rb = create_ring_buffer(capacity);
    EXPECT_TRUE(rb != NULL);
    EXPECT_EQ(rb->capacity, capacity);
    EXPECT_EQ(rb->count, 0);

    const char *str = "hello";
    const size_t len = strlen(str);
    push_data_in_ring_buffer(rb, str, len);

    EXPECT_EQ(rb->count, len);

    char *new_str = pop_data_from_ring_buffer(rb, len);

    ASSERT_TRUE(new_str != NULL);
    ASSERT_STREQ(new_str, str);
    free(new_str);
    EXPECT_EQ(rb->head, rb->tail);
    EXPECT_EQ(rb->count, 0);
}

TEST(RingBuffer, PushAndPopData_TwoPushes)
{
    const size_t capacity = 150;
    ring_buffer_t *rb = create_ring_buffer(capacity);

    const char *str = "hello";
    const size_t len = strlen(str);
    push_data_in_ring_buffer(rb, str, len);
    const char *str2 = "everyone";
    const size_t len2 = strlen(str2);
    push_data_in_ring_buffer(rb, str2, len2);

    EXPECT_EQ(rb->count, len + len2);

    char *new_str = pop_data_from_ring_buffer(rb, len);

    ASSERT_TRUE(new_str != NULL);
    ASSERT_STREQ(new_str, str);
    free(new_str);
    EXPECT_EQ(rb->count, len2);

    char *new_str_2 = pop_data_from_ring_buffer(rb, len2);

    ASSERT_TRUE(new_str_2 != NULL);
    ASSERT_STREQ(new_str_2, str2);
    free(new_str_2);

    EXPECT_EQ(rb->head, rb->tail);
    EXPECT_EQ(rb->count, 0);
}
