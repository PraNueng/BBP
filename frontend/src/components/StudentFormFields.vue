<template>
  <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
    <!-- ชื่อ -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">
        ชื่อ<span class="text-red-500">*</span>
      </label>
      <input
        :value="modelValue.firstName"
        @input="updateField('firstName', $event.target.value)"
        type="text"
        required
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="กรอกชื่อจริงของน้อง"
      />
    </div>

    <!-- นามสกุล -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">นามสกุล</label>
      <input
        :value="modelValue.lastName"
        @input="updateField('lastName', $event.target.value)"
        type="text"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="กรอกนามสกุลของน้อง"
      />
    </div>

    <!-- ชื่อเล่น -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">ชื่อเล่น</label>
      <input
        :value="modelValue.nickname"
        @input="updateField('nickname', $event.target.value)"
        type="text"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="กรอกชื่อเล่นของน้อง"
      />
    </div>

    <!-- โรงเรียน -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">โรงเรียน</label>
      <input
        :value="modelValue.schoolName"
        @input="updateField('schoolName', $event.target.value)"
        type="text"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="เช่น บางปะกอกวิทยาคม"
      />
    </div>

    <!-- ชั้นเรียน -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">
        ชั้นเรียน <span class="text-red-500">*</span>
      </label>
      <select
        :value="modelValue.gradeId"
        @change="updateField('gradeId', parseInt($event.target.value) || null)"
        :disabled="disableGradeChange"
        required
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 transition-all"
        :class="{
          'bg-gray-100 cursor-not-allowed opacity-70': disableGradeChange,
          'hover:border-blue-400': !disableGradeChange
        }"
      >
        <option value="">-- เลือกชั้นเรียน --</option>
        <option value="1">ม.1</option>
        <option value="2">ม.2</option>
        <option value="3">ม.3</option>
        <option value="4">ม.4</option>
        <option value="5">ม.5</option>
        <option value="6">ม.6</option>
        <option value="7">อื่น ๆ</option>
      </select>
      <p v-if="disableGradeChange" class="mt-2 text-sm text-orange-600 flex items-center gap-1">
        <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        <span>ไม่สามารถแก้ไขชั้นเรียนได้</span>
      </p>
    </div>
    
    <!-- แผนการเรียน -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">แผนการเรียน</label>
      <textarea
        :value="modelValue.studyPlan"
        @input="updateField('studyPlan', $event.target.value)"
        rows="1"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="เช่น วิทย์-คณิต"
      ></textarea>
    </div>

    <!-- เบอร์นักเรียน -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">เบอร์นักเรียน</label>
      <input
        :value="modelValue.phoneStudent"
        @input="updateField('phoneStudent', $event.target.value)"
        type="text"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="09xxxxxxxx"
      />
    </div>

    <!-- เบอร์ผู้ปกครอง -->
    <div>
      <label class="block text-gray-700 font-semibold mb-2">เบอร์ผู้ปกครอง</label>
      <input
        :value="modelValue.phoneParent"
        @input="updateField('phoneParent', $event.target.value)"
        type="text"
        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        placeholder="09xxxxxxxx"
      />
    </div>

    <!-- Classes Section (Optional) -->
    <div class="md:col-span-3 bg-blue-50 border border-blue-200 p-4 rounded-lg" v-if="showClassSelector">
      <h3 class="font-semibold text-blue-700 mb-2">เลือกคลาสที่ต้องการ</h3>
      
      <div v-for="(cls, index) in modelValue.classes" :key="index" 
        class="grid grid-cols-1 md:grid-cols-5 gap-3 mb-3 p-3 border rounded-lg bg-white">
        
        <!-- เลือกประเภทคลาส -->
        <div>
          <label class="font-semibold text-sm">ประเภทคลาส</label>
          <select 
            :value="cls.classType" 
            @change="handleClassTypeChange(index, $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 text-sm"
          >
            <option disabled value="">-- เลือกประเภท --</option>
            <option value="MONTH">รายเดือน</option>
            <option value="HOUR">รายชั่วโมง</option>
          </select>
        </div>

        <!-- เลือกโหมด -->
        <div v-if="cls.classType" class="flex flex-col gap-2">
          <label class="font-semibold text-sm">โหมด</label>
          <div class="flex gap-4">
            <label class="flex items-center cursor-pointer">
              <input 
                type="radio" 
                :name="'mode-' + index"
                :value="'GROUP'" 
                :checked="cls.mode === 'GROUP'"
                @change="updateClass(index, 'mode', 'GROUP')"
                class="mr-1"
              /> 
              <span class="text-sm">กลุ่ม</span>
            </label>
            <label class="flex items-center cursor-pointer">
              <input 
                type="radio" 
                :name="'mode-' + index"
                :value="'PRIVATE'" 
                :checked="cls.mode === 'PRIVATE'"
                @change="updateClass(index, 'mode', 'PRIVATE')"
                :disabled="cls.classType === 'MONTH'"
                class="mr-1"
              /> 
              <span class="text-sm">เดี่ยว</span>
            </label>
          </div>
        </div>

        <!-- ถ้ารายเดือน + กลุ่ม → เลือกตาราง -->
        <div v-if="cls.classType === 'MONTH' && cls.mode === 'GROUP'">
          <label class="font-semibold text-sm">เวลาเรียน</label>
          <select 
            :value="cls.schedule" 
            @change="updateClass(index, 'schedule', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 text-sm"
          >
            <option disabled value="">-- เลือกเวลา --</option>
            <option value="MON-FRI">จันทร์-ศุกร์</option>
            <option value="TUE-THU">อังคาร-พฤหัส</option>
            <option value="SAT">เสาร์</option>
            <option value="SUN">อาทิตย์</option>
          </select>
        </div>

        <!-- ถ้ารายชั่วโมง + กลุ่ม → เลือกประเภทกลุ่ม -->
        <div v-if="cls.classType === 'HOUR' && cls.mode === 'GROUP'">
          <label class="font-semibold text-sm">ประเภทกลุ่ม</label>
          <select 
            :value="cls.groupType" 
            @change="updateClass(index, 'groupType', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 text-sm"
          >
            <option disabled value="">-- เลือกประเภท --</option>
            <option value="A">A</option>
            <option value="B">B</option>
            <option value="C">C</option>
            <option value="PV">PV</option>
          </select>
        </div>

        <!-- ถ้าเป็นรายชั่วโมง → ใส่จำนวนชั่วโมง -->
        <div v-if="cls.classType === 'HOUR'">
          <label class="font-semibold text-sm">จำนวนชั่วโมง</label>
          <input 
            :value="cls.hours" 
            @input="updateClass(index, 'hours', $event.target.value)"
            type="number" 
            min="1" 
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 text-sm" 
            placeholder="ชม."
          />
        </div>

        <!-- ลบ -->
        <div class="flex items-end">
          <button 
            type="button" 
            @click="removeClass(index)" 
            class="w-full px-3 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition text-sm"
          >
            ลบ
          </button>
        </div>
      </div>

      <button 
        type="button" 
        @click="addNewClass" 
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
      >
        + เพิ่มคลาส
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  showClassSelector: {
    type: Boolean,
    default: true
  },
  disableGradeChange: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue']);

const updateField = (field, value) => {
  emit('update:modelValue', {
    ...props.modelValue,
    [field]: value
  });
};

const updateClass = (index, field, value) => {
  const updatedClasses = [...props.modelValue.classes];
  updatedClasses[index] = {
    ...updatedClasses[index],
    [field]: value
  };
  emit('update:modelValue', {
    ...props.modelValue,
    classes: updatedClasses
  });
};

const handleClassTypeChange = (index, value) => {
  const updatedClasses = [...props.modelValue.classes];
  updatedClasses[index] = {
    ...updatedClasses[index],
    classType: value,
    mode: value === 'MONTH' ? 'GROUP' : null,
    schedule: null,
    groupType: null,
    hours: null
  };
  emit('update:modelValue', {
    ...props.modelValue,
    classes: updatedClasses
  });
};

const addNewClass = () => {
  const updatedClasses = [
    ...props.modelValue.classes,
    {
      classType: '',
      mode: '',
      schedule: null,
      groupType: null,
      hours: null
    }
  ];
  emit('update:modelValue', {
    ...props.modelValue,
    classes: updatedClasses
  });
};

const removeClass = (index) => {
  const updatedClasses = props.modelValue.classes.filter((_, i) => i !== index);
  emit('update:modelValue', {
    ...props.modelValue,
    classes: updatedClasses
  });
};
</script>