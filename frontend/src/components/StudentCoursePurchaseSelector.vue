<template>
  <div class="grid grid-cols-1 gap-6">
    <div>
      <!-- <div class="flex items-center gap-3 mb-6">
        <div v-if="props.showHistoryButton" class="flex-grow flex justify-end">
          <button 
            @click.stop.prevent="$emit('show-history')"
            class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-xl hover:from-purple-600 hover:to-pink-700 transition-all font-semibold shadow-md flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            ประวัติการซื้อคอร์ส
          </button>
        </div>
      </div> -->
      
      <div v-if="isLoadingOptions" class="text-center py-12">
        <div class="inline-block relative">
          <div class="animate-spin rounded-full h-16 w-16 border-4 border-blue-200"></div>
          <div class="absolute top-0 left-0 animate-spin rounded-full h-16 w-16 border-4 border-transparent border-t-blue-600"></div>
        </div>
        <p class="text-sm text-gray-600 mt-4 font-medium">กำลังโหลดตัวเลือก...</p>
      </div>

      <div v-else class="space-y-4">
        <!-- คอร์สเก่า (Read-only) -->
        <div v-if="existingCourses.length > 0" class="space-y-4">
          <h4 class="text-sm font-bold text-gray-600 flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-green-600"></span>
              คอร์สที่มีอยู่แล้ว
          </h4>
            
          <!-- คอร์สเก่า -->
          <div v-for="(course, index) in existingCourses" :key="'existing-' + course.id" 
            class="group relative overflow-hidden p-5 rounded-xl border-2 shadow-md transition-all"
            :class="{
              'bg-red-50 border-red-200': course.editReason === 'แอดมินกรอกผิด' || course.editReason === 'อื่น ๆ (แจ้งหัวหน้า)',
              'bg-gray-50 border-gray-300': course.editReason === 'น้องขอจบคอร์ส',
              'bg-white border-blue-200': !course.editReason || (course.editReason !== 'แอดมินกรอกผิด' && course.editReason !== 'อื่น ๆ (แจ้งหัวหน้า)' && course.editReason !== 'น้องขอจบคอร์ส')
            }"
          >
            
            <!-- Grid Layout: Fields + Action Buttons -->
            <div class="grid grid-cols-1 md:grid-cols-[1fr_1fr_1fr_96px] gap-4 items-start">
              
              <!-- วิชา -->
              <div class="space-y-2">
                <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                  <span class="w-1.5 h-1.5 rounded-full bg-blue-600"></span>
                  วิชา
                  <span class="text-red-500">*</span>
                </label>
                <select 
                  :disabled="true"
                  :value="course.subjectId" 
                  @change="updateCourse(index, 'subjectId', $event.target.value)"
                  class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
                >
                  <option disabled value="">-- เลือกวิชา --</option>
                  <option 
                    v-for="subject in subjects" 
                    :key="subject.id" 
                    :value="subject.id"
                  >
                    {{ subject.subjectName }}
                  </option>
                </select>
              </div>

              <!-- ประเภทคลาส -->
              <div class="space-y-2">
                <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                  <span class="w-1.5 h-1.5 rounded-full bg-purple-600"></span>
                  ประเภทคลาส
                  <span class="text-red-500">*</span>
                </label>
                <select 
                  :value="course.classType" 
                  @change="updateCourse(index, 'classType', $event.target.value)"
                  :disabled="true"
                  class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
                >
                  <option value="">-- เลือกประเภท --</option>
                  <option value="GROUP">กลุ่มรวม</option>
                  <option value="INDIVIDUAL">PV-เดี่ยว</option>
                  <option value="INDIVIDUAL_GROUP">PV-กลุ่ม</option>
                  <option value="MONTHLY">รายเดือน</option>
                </select>
                <p v-if="course.hoursCompleted > 0" class="text-xs text-orange-600 ml-2">
                  ⚠️ ไม่สามารถเปลี่ยนประเภทได้เนื่องจากมีการเรียนไปแล้ว
                </p>
              </div>

              <!-- ประเภทคลาสรายเดือน (แสดงเฉพาะเมื่อเลือก MONTHLY) -->
              <div v-if="course.classType === 'MONTHLY'" class="space-y-2">
                <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                  <span class="w-1.5 h-1.5 rounded-full bg-pink-600"></span>
                  กลุ่ม
                  <span class="text-red-500">*</span>
                </label>
                <select 
                  :value="course.monthlySubtypeId" 
                  @change="updateCourse(index, 'monthlySubtypeId', $event.target.value)"
                  :disabled="true"
                  class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
                >
                  <option value="">-- เลือกกลุ่ม --</option>
                  <option 
                    v-for="subtype in monthlySubtypes" 
                    :key="subtype.id" 
                    :value="subtype.id"
                  >
                    {{ subtype.subtypeName }}
                  </option>
                </select>
              </div>

              <!-- จำนวนชั่วโมง -->
              <div v-if="course.classType && course.classType !== 'MONTHLY'" class="space-y-2">
                <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                  <span class="w-1.5 h-1.5 rounded-full bg-yellow-600"></span>
                  จำนวนชั่วโมงที่ซื้อ
                  <span class="text-red-500">*</span>
                </label>
                <input 
                  :disabled="true"
                  :value="course.hoursPurchased" 
                  @input="updateCourse(index, 'hoursPurchased', $event.target.value)"
                  type="number" 
                  min="0.5" 
                  step="0.5"
                  class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  placeholder="เช่น 10"
                />
              </div>

              <!-- Action Buttons (ฝั่งขวา) -->
              <div class="flex items-center justify-center gap-2 h-full mt-3">
                <button
                  type="button"
                  @click="startEditing(index)"
                  class="p-3.5 rounded-3xl bg-blue-100 hover:bg-blue-200 text-blue-700 transition-all"
                  title="แก้ไขคอร์ส"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
              </div>

              <!-- แสดงเหตุผลการแก้ไข (ถ้ามี) -->
              <div v-if="course.editReason" class="col-span-full mt-3 flex justify-end">
                <div class="px-4 py-2 rounded-lg text-xs font-semibold flex items-center gap-2 shadow-sm"
                  :class="{
                    'bg-red-50 text-red-700 border border-red-200': course.editReason === 'แอดมินกรอกผิด' || course.editReason === 'อื่น ๆ (แจ้งหัวหน้า)',
                    'bg-gray-100 text-gray-600 border border-gray-300': course.editReason === 'น้องขอจบคอร์ส',
                    'bg-blue-50 text-blue-700 border border-blue-200': course.editReason === 'น้องกลับมาเรียน'
                  }"
                >
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ course.editReason }}
                </div>
              </div>

              <!-- Dropdown หมายเหตุ -->
              <div v-if="editingCourseIndex === index" class="col-span-full mt-4 p-5 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl border-2 border-blue-300 shadow-inner">
                <div class="flex items-center gap-4">
                  <div class="flex-1">
                    <label class="flex items-center gap-2 text-sm font-bold text-gray-700 mb-2">
                      <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      เลือกเหตุผลในการแก้ไข
                      <span class="text-red-500">*</span>
                    </label>
                    <select 
                      v-model="selectedEditReason"
                      class="w-full px-4 py-3 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium bg-white shadow-sm hover:shadow-md transition-all"
                    >
                      <option value="">-- เลือกเหตุผล --</option>
                      <option v-for="reason in editReasons" :key="reason" :value="reason">
                        {{ reason }}
                      </option>
                    </select>
                  </div>
                  
                  <div class="flex gap-2 self-end pb-0.5">
                    <button
                      type="button"
                      @click="confirmEdit(index)"
                      :disabled="!selectedEditReason"
                      class="px-5 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl hover:from-green-600 hover:to-green-700 disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed font-semibold shadow-md hover:shadow-lg transition-all flex items-center gap-2"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                      ตกลง
                    </button>
                    <button
                      type="button"
                      @click="cancelEditing"
                      class="px-5 py-3 bg-gradient-to-r from-gray-400 to-gray-500 text-white rounded-xl hover:from-gray-500 hover:to-gray-600 font-semibold shadow-md hover:shadow-lg transition-all flex items-center gap-2"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                      ยกเลิก
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Badge แสดงสถานะ -->
            <div class="absolute top-3 right-3">
              <span class="px-3 py-1.5 bg-gray-100 text-gray-700 text-xs font-bold rounded-full border border-gray-300 flex items-center gap-1.5">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
                ไม่สามารถแก้ไขได้
              </span>
            </div>
            <!-- <div v-if="course.confirmed" class="absolute top-3 right-3">
              <span class="px-3 py-1.5 bg-green-100 text-green-700 text-xs font-bold rounded-full border border-green-300 flex items-center gap-1.5">
                <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                </svg>
                ยืนยันแล้ว
              </span>
            </div> -->
          </div>
        </div>

        <!-- คอร์สใหม่ -->
        <div v-for="(course, index) in newCourses" :key="'new-' + index" 
          class="group relative overflow-hidden p-5 rounded-xl border-2 transition-all duration-300 hover:shadow-xl bg-white border-blue-200 hover:border-blue-400 shadow-md hover:-translate-y-1">
          
          <div class="grid grid-cols-1 md:grid-cols-[1fr_1fr_1fr_96px] gap-4 items-start">
            
            <!-- วิชา -->
            <div class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-blue-600"></span>
                วิชา
                <span class="text-red-500">*</span>
              </label>
              <select 
                :disabled="course.confirmed"
                :value="course.subjectId" 
                @change="updateCourse(existingCourses.length + index, 'subjectId', $event.target.value)"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
              >
                <option disabled value="">-- เลือกวิชา --</option>
                <option 
                  v-for="subject in subjects" 
                  :key="subject.id" 
                  :value="subject.id"
                >
                  {{ subject.subjectName }}
                </option>
              </select>
            </div>

            <!-- ประเภทคลาส -->
            <div class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-purple-600"></span>
                ประเภทคลาส
                <span class="text-red-500">*</span>
              </label>
              <select 
                :value="course.classType" 
                @change="updateCourse(existingCourses.length + index, 'classType', $event.target.value)"
                :disabled="course.confirmed"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
              >
                <option value="">-- เลือกประเภท --</option>
                <option value="GROUP">กลุ่มรวม</option>
                <option value="INDIVIDUAL">PV-เดี่ยว</option>
                <option value="INDIVIDUAL_GROUP">PV-กลุ่ม</option>
                <option value="MONTHLY">รายเดือน</option>
              </select>
            </div>

            <!-- กลุ่มรายเดือน (แสดงเฉพาะเมื่อเลือก MONTHLY) -->
            <div v-if="course.classType === 'MONTHLY'" class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-pink-600"></span>
                กลุ่ม
                <span class="text-red-500">*</span>
              </label>
              <select 
                :value="course.monthlySubtypeId" 
                @change="updateCourse(existingCourses.length + index, 'monthlySubtypeId', $event.target.value)"
                :disabled="course.confirmed"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md bg-white border-gray-200 hover:border-blue-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
              >
                <option value="">-- เลือกกลุ่ม --</option>
                <option 
                  v-for="subtype in monthlySubtypes" 
                  :key="subtype.id" 
                  :value="subtype.id"
                >
                  {{ subtype.subtypeName }}
                </option>
              </select>
            </div>

            <!-- จำนวนชั่วโมง (แสดงเฉพาะเมื่อไม่ใช่ MONTHLY) -->
            <div v-if="course.classType && course.classType !== 'MONTHLY'" class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-indigo-600"></span>
                จำนวนชั่วโมงที่ซื้อ
                <span class="text-red-500">*</span>
              </label>
              <input 
                :value="course.hoursPurchased" 
                readonly
                type="number" 
                class="w-full px-4 py-2.5 border-2 rounded-xl text-sm font-medium bg-gray-100 cursor-not-allowed"
                placeholder="เลือกประเภทคลาสก่อน"
              />
            </div>

            <!-- Action Buttons -->
            <div class="mt-7 flex gap-2 justify-start">
              <template v-if="!course.confirmed">
                <button
                  type="button"
                  @click="confirmCourse(existingCourses.length + index)"
                  :disabled="!isValidCourse(course)"
                  class="p-3.5 rounded-3xl bg-green-100 hover:bg-green-200 text-green-700 transition-all disabled:opacity-25 disabled:cursor-not-allowed"
                  title="ยืนยันคอร์ส"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                </button>
                <button
                  type="button"
                  @click="removeCourse(existingCourses.length + index)"
                  class="p-3.5 rounded-3xl bg-red-100 hover:bg-red-200 text-red-700 transition-all"
                  title="ลบคอร์ส"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </template>

              <button
                v-else
                type="button"
                @click="editCourse(existingCourses.length + index)"
                class="p-3.5 rounded-3xl bg-yellow-200 hover:bg-yellow-400 text-yellow-700 transition-all ml-6"
                title="แก้ไขคอร์ส"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
            </div>
          </div>

          <div v-if="course.confirmed" class="absolute top-3 right-3">
            <span class="px-3 py-1.5 bg-green-100 text-green-700 text-xs font-bold rounded-full border border-green-300 flex items-center gap-1.5">
              <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
              ยืนยันแล้ว
            </span>
          </div>
        </div>

        <!-- ปุ่มเพิ่มคอร์ส -->
        <button 
          type="button" 
          @click="addNewCourse" 
          class="group px-6 py-3.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 flex items-center justify-center gap-3 font-bold shadow-lg hover:shadow-xl hover:-translate-y-1 w-full md:w-auto"
        >
          <div class="p-1.5 bg-white/20 rounded-lg group-hover:bg-white/30 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <span class="text-base">เพิ่มคอร์ส</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '@/api.js';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
    readOnlyMode: {
    type: Boolean,
    default: false
  },
  showHistoryButton: {
    type: Boolean,
    default: true
  }
});

const existingCourses = computed(() => {
  return props.modelValue.filter(course => course.id);
});

const newCourses = computed(() => {
  return props.modelValue.filter(course => !course.id);
});

const getSubjectName = (subjectId) => {
  const subject = subjects.value.find(s => s.id === subjectId);
  return subject ? subject.subjectName : '-';
};

const isValidCourse = (course) => {
  if (!course.subjectId || !course.classType) return false;
  
  // ถ้าเป็น MONTHLY ต้องมี monthlySubtypeId
  if (course.classType === 'MONTHLY') {
    return !!course.monthlySubtypeId;
  }
  
  // ถ้าไม่ใช่ MONTHLY ต้องมี hoursPurchased
  return !!course.hoursPurchased;
};

const getClassTypeLabel = (classType) => {
  if (classType === 'GROUP') return 'กลุ่มรวม';
  if (classType === 'INDIVIDUAL') return 'PV-เดี่ยว';
  if (classType === 'INDIVIDUAL_GROUP') return 'PV-กลุ่ม';
  return '-';
};

const emit = defineEmits(['update:modelValue', 'validate', 'duplicate-error', 'show-history']);

const checkDuplicateCourse = () => {
  const courseMap = new Map();
  
  for (let i = 0; i < props.modelValue.length; i++) {
    const course = props.modelValue[i];
    
    // Skip if not filled yet
    if (!course.subjectId || !course.classType) continue;
    
    // Create unique key: subjectId + classType
    const key = `${course.subjectId}-${course.classType}`;
    
    if (courseMap.has(key)) {
      // พบคอร์สซ้ำ
      return {
        isDuplicate: true,
        index1: courseMap.get(key),
        index2: i,
        subjectId: course.subjectId,
        classType: course.classType
      };
    }
    
    courseMap.set(key, i);
  }
  
  return { isDuplicate: false };
};

// เพิ่มฟังก์ชันนี้หลัง checkDuplicateCourse
const checkDuplicateInArray = (coursesArray) => {
  const courseMap = new Map();
  
  for (let i = 0; i < coursesArray.length; i++) {
    const course = coursesArray[i];
    
    // Skip if not filled yet
    if (!course.subjectId || !course.classType) continue;
    
    const key = `${course.subjectId}-${course.classType}`;
    
    if (courseMap.has(key)) {
      return {
        isDuplicate: true,
        index1: courseMap.get(key),
        index2: i,
        subjectId: course.subjectId,
        classType: course.classType
      };
    }
    
    courseMap.set(key, i);
  }
  
  return { isDuplicate: false };
};

const validateCourses = () => {
  // ตรวจสอบข้อมูลครบถ้วน
  const isComplete = props.modelValue.every(course => {
    return course.subjectId && 
           course.hoursPurchased && 
           course.classType;
  });
  
  if (!isComplete) return false;
  
  // ตรวจสอบ duplicate
  const duplicateCheck = checkDuplicateCourse();
  if (duplicateCheck.isDuplicate) {
    emit('duplicate-error', duplicateCheck);
    return false;
  }
  
  return true;
};

const confirmCourse = (index) => {
  const updated = [...props.modelValue];
  updated[index] = {
    ...updated[index],
    confirmed: true
  };
  emit('update:modelValue', updated);
};

const editCourse = (index) => {
  const updated = [...props.modelValue];
  updated[index] = {
    ...updated[index],
    confirmed: false
  };
  emit('update:modelValue', updated);
};

// ข้อมูลจาก API
const subjects = ref([]);
const monthlySubtypes = ref([]);
const isLoadingOptions = ref(false);
const editingCourseIndex = ref(null);
const selectedEditReason = ref('');
const editReasons = [
  'แอดมินกรอกผิด',
  'น้องขอจบคอร์ส',
  'น้องกลับมาเรียน',
  'อื่น ๆ (แจ้งหัวหน้า)'
];

// โหลดข้อมูลวิชา
const loadOptions = async () => {
  isLoadingOptions.value = true;
  try {
    const subjectsRes = await api.get('/subjects/all');
    const monthlyRes = await api.get('/monthly-subtypes/all');
    
    // กรองเฉพาะที่ isActive: true
    subjects.value = subjectsRes.data.filter(item => item.isActive);
    monthlySubtypes.value = monthlyRes.data.filter(item => item.isActive);
  } catch (error) {
    console.error('Error loading subjects:', error);
  } finally {
    isLoadingOptions.value = false;
  }
};

const startEditing = (index) => {
  editingCourseIndex.value = index;
  selectedEditReason.value = '';
};

const cancelEditing = () => {
  editingCourseIndex.value = null;
  selectedEditReason.value = '';
};

const confirmEdit = (index) => {
  if (!selectedEditReason.value) return;
  
  const updated = [...props.modelValue];
  updated[index] = {
    ...updated[index],
    editReason: selectedEditReason.value,
    hasEditReason: true
  };
  
  emit('update:modelValue', updated);
  cancelEditing();
};

const updateCourse = (index, field, value) => {
  const updatedCourses = [...props.modelValue];
  const currentCourse = { ...updatedCourses[index] };

  const parsedValue =
    field === 'subjectId' || field === 'hoursPurchased' || field === 'monthlySubtypeId'
      ? parseFloat(value) || null
      : value;

  currentCourse[field] = parsedValue;

  // AUTO FILL: ถ้าเลือก "GROUP" และยังไม่มีชั่วโมง
  if (field === 'classType') {
    if (parsedValue === 'GROUP') {
      currentCourse.hoursPurchased = 18;
    } else if (parsedValue === 'INDIVIDUAL' || parsedValue === 'INDIVIDUAL_GROUP') {
      currentCourse.hoursPurchased = 12;
    } else if (parsedValue === 'MONTHLY') {
      currentCourse.hoursPurchased = null;
    }
  }

  updatedCourses[index] = currentCourse;

  // ตรวจ duplicate
  const duplicateCheck = checkDuplicateInArray(updatedCourses);
  if (duplicateCheck.isDuplicate) {
    emit('duplicate-error', duplicateCheck);
    return;
  }

  emit('update:modelValue', updatedCourses);
};

// เพิ่มคอร์สใหม่
const addNewCourse = () => {
  const updatedCourses = [
    ...props.modelValue,
    {
      subjectId: '',
      hoursPurchased: null,
      classType: '',
      monthlySubtypeId: null,
      confirmed: false
    }
  ];
  emit('update:modelValue', updatedCourses);
};

// ลบคอร์ส
const removeCourse = (index) => {
  const updatedCourses = props.modelValue.filter((_, i) => i !== index);
  emit('update:modelValue', updatedCourses);
  emit('validate', validateCourses());
};

onMounted(() => {
  loadOptions();
});
</script>