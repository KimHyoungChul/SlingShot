//log
#include <utils/Log.h>


#ifndef ENABLE_LOG
#define ENABLE_LOG 1
#endif

#ifndef SLINGSHOT_LOGE
#if ENABLE_LOG
#define SLINGSHOT_LOGE (void)LOGE
#else
#define SLINGSHOT_LOGE(...)
#endif
#endif

#ifndef SLINGSHOT_LOGD
#if ENABLE_LOG
#define SLINGSHOT_LOGD (void)LOGD
#else
#define SLINGSHOT_LOGD(...) 
#endif
#endif

#ifndef SLINGSHOT_LOGV
#if ENABLE_LOG
#define SLINGSHOT_LOGV (void)LOGV
#else
#define SLINGSHOT_LOGV(...)
#endif
#endif

