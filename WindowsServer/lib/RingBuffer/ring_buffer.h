
#ifndef __RING_BUFFER_H__
#define __RING_BUFFER_H__

#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct ring_buffer_s
{
    char *buffer;
    char *end;

    size_t capacity;
    size_t count;

    char *head;
    char *tail;

} ring_buffer_t;

ring_buffer_t *create_ring_buffer(size_t capacity);
void push_data_in_ring_buffer(ring_buffer_t *rb, char const *data, size_t len);
/*
No check is done if the tail passes the head.
*/
char *pop_data_from_ring_buffer(ring_buffer_t *rb, size_t len);
void delete_ring_buffer(ring_buffer_t *rb);

/*
Does not update the count element !
It just allows to gp to the start of the buffer when we are at the end.
*/
void move_next(ring_buffer_t *rb, char **ptr);

void move_tail(ring_buffer_t *rb);

#ifdef __cplusplus
}
#endif

#endif
