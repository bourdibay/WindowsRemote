
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>

#include "ring_buffer.h"

ring_buffer_t *create_ring_buffer(size_t capacity)
{
    ring_buffer_t *rb = malloc(sizeof(ring_buffer_t));

    if (rb == NULL) {
        return NULL;
    }

    rb->capacity = capacity;
    rb->buffer = malloc(sizeof(char) * rb->capacity);
    if (rb->buffer == NULL) {
        free(rb);
        return NULL;
    }
    rb->head = rb->buffer;
    rb->tail = rb->buffer;
    rb->end = rb->buffer + rb->capacity;
    rb->count = 0;
    return rb;
}

void delete_ring_buffer(ring_buffer_t *rb)
{
    free(rb->buffer);
    free(rb);
}

/*
TODO: would be much better with memmove()...
*/

void push_data_in_ring_buffer(ring_buffer_t *rb, char const *data, size_t len)
{
    size_t counter = 0;

    while (counter < len) {
        *rb->head = data[counter];
        move_next(rb, &rb->head);
        ++counter;
        ++rb->count;
    }
#if defined _DEBUG
    printf("Push into ring buffer count=%zu\n", rb->count);
    printf("Buffer is [%.*s]\n", (int)rb->capacity, rb->buffer);
#endif
}

char *pop_data_from_ring_buffer(ring_buffer_t *rb, size_t len)
{
    size_t counter = 0;
    char *data = malloc(sizeof(char) * (len + 1));

    if (data == NULL) {
        return NULL;
    }
    while (counter < len) {
        data[counter] = *rb->tail;
        move_tail(rb);
        ++counter;
    }
#if defined _DEBUG
    printf("Pop from ring buffer count=%zu\n", rb->count);
    printf("Buffer is [%.*s]\n", (int)rb->capacity, rb->buffer);
#endif
    data[counter] = '\0';
    return data;
}

void move_next(ring_buffer_t *rb, char **ptr)
{
    *ptr = *ptr + 1;
    if (*ptr == rb->end) {
        *ptr = rb->buffer;
    }
}

void move_tail(ring_buffer_t *rb)
{
    move_next(rb, &rb->tail);
    --rb->count;
}
