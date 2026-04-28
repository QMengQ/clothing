<template>
  <el-dialog
    v-model="dialogVisible"
    title="支付订单"
    width="500px"
    :close-on-click-modal="true"
    :close-on-press-escape="true"
  >
    <div class="payment-container" v-if="order && order.id">
      <!-- 订单信息 -->
      <div class="order-info">
        <h3>订单信息</h3>
        <p>订单号: {{ order.id }}</p>
        <p>商品: {{ order.clothing?.title || '未知' }}</p>
        <p>金额: ¥{{ order.price?.toFixed(2) || '0.00' }}</p>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-method">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="selectedPaymentMethod">
          <el-radio-button label="wechat">微信支付</el-radio-button>
          <el-radio-button label="alipay">支付宝</el-radio-button>
          <el-radio-button label="card">银行卡</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 支付状态 -->
      <div v-if="paymentStatus !== 'init'" class="payment-status">
        <el-alert
          :title="paymentStatusText"
          :type="paymentStatusType"
          :closable="false"
          class="status-alert"
        />
        
        <!-- 加载动画 -->
        <div v-if="paymentStatus === 'processing'" class="loading-container">
          <el-icon class="is-loading"><Refresh /></el-icon>
          <span>正在处理支付...</span>
        </div>

        <!-- 支付失败重试 -->
        <div v-if="paymentStatus === 'failed'" class="retry-container">
          <el-button type="primary" @click="retryPayment">重新支付</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <span v-if="paymentStatus === 'init'">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment" :loading="confirmLoading">
            确认支付
          </el-button>
        </span>
        <span v-else-if="paymentStatus === 'success'">
          <el-button type="primary" @click="dialogVisible = false">
            关闭
          </el-button>
        </span>
        <span v-else>
          <el-button @click="dialogVisible = false">取消</el-button>
        </span>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '../utils/request'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    default: () => {}
  }
})

// Emits
const emit = defineEmits(['update:visible', 'payment-success'])

// Dialog visible
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// Payment method
const selectedPaymentMethod = ref('wechat')

// Payment status
const paymentStatus = ref('init') // init, processing, success, failed
const confirmLoading = ref(false)

// Payment status text
const paymentStatusText = computed(() => {
  switch (paymentStatus.value) {
    case 'processing':
      return '支付处理中，请稍候...'
    case 'success':
      return '支付成功！'
    case 'failed':
      return '支付失败，请重试'
    default:
      return ''
  }
})

// Payment status type
const paymentStatusType = computed(() => {
  switch (paymentStatus.value) {
    case 'processing':
      return 'info'
    case 'success':
      return 'success'
    case 'failed':
      return 'error'
    default:
      return 'info'
  }
})

// Confirm payment
const confirmPayment = async () => {
  if (!props.order || !props.order.id) {
    ElMessage.error('订单信息错误')
    return
  }

  confirmLoading.value = true
  paymentStatus.value = 'processing'

  try {
    // 调用后端支付接口
    const response = await request.put(`/api/v1/orders/${props.order.id}/pay`, {
      paymentMethod: selectedPaymentMethod.value
    })

    paymentStatus.value = 'success'
    ElMessage.success('支付成功')
    emit('payment-success')
  } catch (error) {
    console.error('支付失败:', error)
    paymentStatus.value = 'failed'
    ElMessage.error('支付失败，请检查网络连接后重试')
  } finally {
    confirmLoading.value = false
  }
}

// Retry payment
const retryPayment = () => {
  paymentStatus.value = 'init'
}

// Watch for visible changes
watch(() => props.visible, (newValue) => {
  if (newValue) {
    // Reset status when dialog opens
    paymentStatus.value = 'init'
    selectedPaymentMethod.value = 'wechat'
    confirmLoading.value = false
  }
})
</script>

<style scoped>
.payment-container {
  padding: 20px 0;
}

.order-info {
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.order-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
}

.order-info p {
  margin: 5px 0;
  font-size: 14px;
}

.payment-method {
  margin-bottom: 30px;
}

.payment-method h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.status-alert {
  margin: 20px 0;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-size: 14px;
}

.loading-container .el-icon {
  margin-right: 10px;
  font-size: 20px;
}

.retry-container {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}
</style>