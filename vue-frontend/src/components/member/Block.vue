<template>
  <v-container>
    <v-icon color = "red" size = "50">mdi-block-helper</v-icon>
    <strong>{{ nickname }}</strong>님의 차단목록
    <p style = "display: inline">사용자 차단은 최대 5명까지 가능합니다.</p>
    <p class = "ml-3" style = "display: inline">{{ blocks.length }} / 5</p>


    <div v-for = "(list,index) in blocks" :key = "index">
      <v-chip
          class = "ml-1 float-left"
          close
          color = "info"
          label
          @click = "goProfile(list.toNickname)"
          @click:close = "deleteBlock(list.toNickname,list.id)">
        {{ list.toNickname }}
      </v-chip>
    </div>

  </v-container>
</template>

<script>
export default {
  name    : "Block",
  data() {
    return {}
  },
  methods : {
    goProfile(toNickname) {
      this.$router.push({
        path : '/profile',
        query: {nickname: toNickname}
      });

    },
    deleteBlock(toNickname, blockId) {
      let data = {
        toNickname: toNickname,
        blockId   : blockId
      }
      this.$store.dispatch('REQUEST_DELETE_BLOCK', data).then(() => {
        this.$store.dispatch('REQUEST_GET_BLOCKS', this.nickname);
      });
    }
  },
  computed: {
    nickname() {
      return this.$store.state.memberStore.nickname;
    },
    blocks() {
      return this.$store.state.memberStore.blocks;
    }
  },
  created() {
  },
  mounted() {
    this.$store.dispatch('REQUEST_GET_BLOCKS', this.nickname);
  }
}
</script>

<style scoped>

</style>