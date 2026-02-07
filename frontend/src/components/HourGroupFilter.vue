<template>
  <div class="relative w-full md:w-auto">
    <button
      @click="showDropdown = !showDropdown"
      class="px-6 py-3 bg-gradient-to-r from-orange-500 to-amber-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-orange-600 hover:to-amber-700 transition-all flex items-center gap-2 w-full md:w-auto justify-center"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      กรองชั่วโมง
    </button>

    <div
      v-if="showDropdown"
      class="absolute top-full mt-2 right-0 bg-white border-2 border-gray-200 rounded-2xl shadow-2xl z-50 w-64"
    >
      <!-- ส่วนหัว -->
      <div
        class="sticky top-0 bg-gradient-to-r from-orange-500 to-amber-600 text-white px-4 py-3 rounded-t-2xl flex justify-between items-center"
      >
        <span class="font-semibold">กรองทุก ๆ กี่ชั่วโมง</span>
        <button
          v-if="internalValue !== null"
          @click="clearSelection"
          class="text-xs bg-white text-orange-600 px-3 py-1 rounded-full hover:bg-gray-100 transition-all font-semibold"
        >
          ล้าง
        </button>
      </div>

      <!-- รายการตัวเลือก -->
      <div class="p-2">
        <label
          v-for="hour in hourOptions"
          :key="hour"
          class="flex items-center gap-3 px-4 py-3 hover:bg-orange-50 rounded-xl cursor-pointer transition-all group"
        >
          <input
            type="radio"
            :value="hour"
            v-model="internalValue"
            @change="onHourChange"
            class="w-5 h-5 text-orange-600 border-2 border-gray-300 focus:ring-0 focus:ring-offset-0 cursor-pointer"
          />
          <span class="text-gray-700 font-medium">{{ hour }} ชั่วโมง</span>
        </label>
      </div>

      <!-- ปุ่มล่าง -->
      <div class="sticky bottom-0 bg-white border-t-2 border-gray-200 px-4 py-3 rounded-b-2xl flex gap-2">
        <button
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-all font-semibold"
        >
          ปิด
        </button>
        <button
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gradient-to-r from-orange-500 to-amber-600 text-white rounded-full hover:from-orange-600 hover:to-amber-700 transition-all font-semibold shadow-md"
        >
          ตกลง
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: null }
})
const emit = defineEmits(['update:modelValue', 'apply'])

const showDropdown = ref(false)
const internalValue = ref(props.modelValue)

const hourOptions = [12, 18]

// sync ค่าเมื่อ parent เปลี่ยน
watch(
  () => props.modelValue,
  val => {
    internalValue.value = val
  }
)

// อัปเดตผลลัพธ์ทันทีเมื่อเลือกชั่วโมง
const onHourChange = () => {
  emit('update:modelValue', internalValue.value)
  emit('apply')
}

// ล้างทั้งหมด + รีเฟรชผลลัพธ์ทันที
const clearSelection = () => {
  internalValue.value = null
  emit('update:modelValue', null)
  emit('apply')
}
</script>