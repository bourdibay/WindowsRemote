
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "ring_buffer_extra.h"

static size_t __find_end_of_msg(ring_buffer_t *rb)
{
    char *original_tail = rb->tail;
    int braces_opened = 1;
    size_t len = 1;

    while (braces_opened > 0)
    {
        move_next(rb, &rb->tail);
        if (rb->tail == rb->head) {
            rb->tail = original_tail; /* Restore */
            return 0; /* Message incomplete */
        }

        if (*rb->tail == '}') {
            --braces_opened;
        }
        else if (*rb->tail == '{') {
            ++braces_opened;
        }
        ++len;
    }
    rb->tail = original_tail; /* Restore */
    return len;
}

char *extract_next_json_msg(ring_buffer_t *rb, size_t *len)
{
    char *msg = NULL;
    size_t current_count = rb->count;

    *len = 0;

    if (*rb->tail != '{' && rb->tail != rb->head) {
        fprintf(stderr, "WARNING: Ignoring some chars:[");
        while (*rb->tail != '{' && rb->tail != rb->head) {
            fprintf(stderr, "%c", *rb->tail);
            move_next(rb, &rb->tail);
            --rb->count;
        }
        fprintf(stderr, "] (= %zd chars)\n", current_count - rb->count);
    }

    if (rb->tail == rb->head) {
        return NULL;
    }

    *len = __find_end_of_msg(rb);
    if (*len == 0) {
        return NULL;
    }

    return pop_data_from_ring_buffer(rb, *len);
}
