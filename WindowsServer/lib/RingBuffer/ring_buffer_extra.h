
#ifndef __RING_BUFFER_EXTRA_H__
#define __RING_BUFFER_EXTRA_H__

#include "ring_buffer.h"

#ifdef __cplusplus
extern "C" {
#endif

char *extract_next_json_msg(ring_buffer_t *rb, size_t *len);

#ifdef __cplusplus
}
#endif

#endif
