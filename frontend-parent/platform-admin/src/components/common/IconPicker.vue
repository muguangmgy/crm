<template>
  <el-popover
      :width="500"
      trigger="click"
      v-model:visible="popoverVisible"
  >
    <template #reference>
      <el-input
          :model-value="modelValue"
          placeholder="点击选择图标"
          readonly
          style="cursor: pointer"
      >
        <template #prefix>
          <el-icon v-if="modelValue" style="margin-right: 4px;">
            <component :is="modelValue"/>
          </el-icon>
        </template>
      </el-input>
    </template>

    <div class="icon-picker">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索图标"
          clearable
          style="margin-bottom: 10px"
      />

      <el-tabs v-model="activeTab" type="card" size="small">
        <el-tab-pane v-for="category in filteredCategories" :key="category.name" :label="category.label"
                     :name="category.name">
          <el-scrollbar height="280px">
            <div class="icon-grid">
              <div
                  v-for="icon in category.icons"
                  :key="icon"
                  class="icon-item"
                  :class="{ 'is-active': modelValue === icon }"
                  @click="handleSelect(icon)"
              >
                <el-icon :size="24">
                  <component :is="icon"/>
                </el-icon>
                <span class="icon-name">{{ icon }}</span>
              </div>
            </div>
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-popover>
</template>

<script setup>
import {computed, ref} from 'vue'

const props = defineProps({
  modelValue: {type: String, default: ''}
})

const emit = defineEmits(['update:modelValue'])

const popoverVisible = ref(false)
const searchKeyword = ref('')
const activeTab = ref('system')

// 图标分类
const iconCategories = [
  {
    name: 'system',
    label: '系统图标',
    icons: [
      'Plus', 'Minus', 'CirclePlus', 'Search', 'Female', 'Male', 'Aim', 'House',
      'FullScreen', 'Loading', 'Link', 'Service', 'Pointer', 'Star', 'Notification',
      'Connection', 'ChatDotRound', 'Setting', 'Clock', 'Position', 'Discount',
      'Odometer', 'ChatSquare', 'ChatRound', 'ChatLineRound', 'ChatLineSquare',
      'ChatDotSquare', 'View', 'Hide', 'Unlock', 'Lock', 'RefreshRight', 'RefreshLeft',
      'Refresh', 'Bell', 'MuteNotification', 'User', 'Check', 'CircleCheck', 'Warning',
      'CircleClose', 'Close', 'PieChart', 'More', 'Compass', 'Filter', 'Switch',
      'Select', 'SemiSelect', 'CloseBold', 'EditPen', 'Edit', 'Message', 'MessageBox',
      'TurnOff', 'Finished', 'Delete', 'Crop', 'SwitchButton', 'Operation', 'Open',
      'Remove', 'ZoomOut', 'ZoomIn', 'InfoFilled', 'CircleCheckFilled', 'SuccessFilled',
      'WarningFilled', 'CircleCloseFilled', 'QuestionFilled', 'WarnTriangleFilled',
      'UserFilled', 'MoreFilled', 'Tools', 'HomeFilled', 'Menu', 'UploadFilled',
      'Avatar', 'HelpFilled', 'Share', 'StarFilled', 'Comment', 'Histogram', 'Grid',
      'Promotion', 'DeleteFilled', 'RemoveFilled', 'CirclePlusFilled'
    ]
  },
  {
    name: 'arrow',
    label: '箭头方向',
    icons: [
      'ArrowLeft', 'ArrowUp', 'ArrowRight', 'ArrowDown', 'ArrowLeftBold', 'ArrowUpBold',
      'ArrowRightBold', 'ArrowDownBold', 'DArrowRight', 'DArrowLeft', 'Download',
      'Upload', 'Top', 'Bottom', 'Back', 'Right', 'TopRight', 'TopLeft', 'BottomRight',
      'BottomLeft', 'Sort', 'SortUp', 'SortDown', 'Rank', 'CaretLeft', 'CaretTop',
      'CaretRight', 'CaretBottom', 'DCaret', 'Expand', 'Fold'
    ]
  },
  {
    name: 'document',
    label: '文档文件',
    icons: [
      'DocumentAdd', 'Document', 'Notebook', 'Tickets', 'Memo', 'Collection',
      'Postcard', 'ScaleToOriginal', 'SetUp', 'DocumentDelete', 'DocumentChecked',
      'DataBoard', 'DataAnalysis', 'CopyDocument', 'FolderChecked', 'Files', 'Folder',
      'FolderDelete', 'FolderRemove', 'FolderOpened', 'DocumentCopy', 'DocumentRemove',
      'FolderAdd', 'FirstAidKit', 'Reading', 'DataLine', 'Management', 'Checked',
      'Ticket', 'Failed', 'TrendCharts', 'List'
    ]
  },
  {
    name: 'media',
    label: '多媒体',
    icons: [
      'Microphone', 'Mute', 'Mic', 'VideoPause', 'VideoCamera', 'VideoPlay',
      'Headset', 'Monitor', 'Film', 'Camera', 'Picture', 'PictureRounded',
      'Iphone', 'Cellphone', 'VideoCameraFilled', 'PictureFilled', 'Platform',
      'CameraFilled', 'BellFilled'
    ]
  },
  {
    name: 'traffic',
    label: '位置交通',
    icons: [
      'Location', 'LocationInformation', 'DeleteLocation', 'Coordinate', 'Bicycle',
      'OfficeBuilding', 'School', 'Guide', 'AddLocation', 'MapLocation', 'Place',
      'LocationFilled', 'Van'
    ]
  },
  {
    name: 'food',
    label: '餐饮食物',
    icons: [
      'Watermelon', 'Pear', 'NoSmoking', 'Smoking', 'Mug', 'GobletSquareFull',
      'GobletFull', 'KnifeFork', 'Sugar', 'Bowl', 'MilkTea', 'Lollipop', 'Coffee',
      'Chicken', 'Dish', 'IceTea', 'ColdDrink', 'CoffeeCup', 'DishDot', 'IceDrink',
      'IceCream', 'Dessert', 'IceCreamSquare', 'ForkSpoon', 'IceCreamRound', 'Food',
      'HotWater', 'Grape', 'Fries', 'Apple', 'Burger', 'Goblet', 'GobletSquare',
      'Orange', 'Cherry'
    ]
  },
  {
    name: 'items',
    label: '物品工具',
    icons: [
      'Printer', 'Calendar', 'CreditCard', 'Box', 'Money', 'Refrigerator', 'Cpu',
      'Football', 'Brush', 'Suitcase', 'SuitcaseLine', 'Umbrella', 'AlarmClock',
      'Medal', 'GoldMedal', 'Present', 'Mouse', 'Watch', 'QuartzWatch', 'Magnet',
      'Help', 'Soccer', 'ToiletPaper', 'ReadingLamp', 'Paperclip', 'MagicStick',
      'Basketball', 'Baseball', 'Coin', 'Goods', 'Sell', 'SoldOut', 'Key',
      'ShoppingCart', 'ShoppingCartFull', 'ShoppingTrolley', 'Phone', 'Scissor',
      'Handbag', 'ShoppingBag', 'Trophy', 'TrophyBase', 'Stopwatch', 'Timer',
      'CollectionTag', 'TakeawayBox', 'PriceTag', 'Wallet', 'Opportunity',
      'PhoneFilled', 'WalletFilled', 'GoodsFilled', 'Flag', 'BrushFilled', 'Briefcase', 'Stamp'
    ]
  },
  {
    name: 'weather',
    label: '天气自然',
    icons: [
      'Sunrise', 'Sunny', 'Ship', 'MostlyCloudy', 'PartlyCloudy', 'Sunset',
      'Drizzling', 'Pouring', 'Cloudy', 'Moon', 'MoonNight', 'Lightning'
    ]
  },
  {
    name: 'other',
    label: '其他',
    icons: [
      'ChromeFilled', 'Eleme', 'ElemeFilled', 'ElementPlus', 'Shop', 'SwitchFilled',
      'WindPower'
    ]
  }
]

// 搜索过滤后的分类
const filteredCategories = computed(() => {
  if (!searchKeyword.value) return iconCategories

  return iconCategories.map(category => ({
    ...category,
    icons: category.icons.filter(icon =>
        icon.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  })).filter(category => category.icons.length > 0)
})

const handleSelect = (icon) => {
  emit('update:modelValue', icon)
  popoverVisible.value = false
}
</script>

<style scoped lang="scss">
.icon-picker {
  .icon-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 8px;
    padding: 4px;
  }

  .icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8px;
    border: 1px solid #EBEEF5;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #409EFF;
      background-color: #ECF5FF;
    }

    &.is-active {
      border-color: #409EFF;
      background-color: #ECF5FF;
    }

    .icon-name {
      font-size: 10px;
      color: #909399;
      margin-top: 4px;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      width: 100%;
      text-align: center;
    }
  }

  :deep(.el-tabs__header) {
    margin-bottom: 10px;
  }

  :deep(.el-tabs__item) {
    padding: 0 12px;
    height: 32px;
    line-height: 32px;
  }
}
</style>