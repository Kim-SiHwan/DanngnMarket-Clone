<template>
  <v-container>

    <v-icon color = "pink" size = "50">mdi-heart</v-icon>
    <strong>{{ nickname }}</strong>님의 관심 상품
    <p><small v-if = "myLikeProductList">{{ myLikeProductList.length }}개의 관심 상품이 있습니다</small></p>

    <div id = "myLikeProductListDiv" class = "row justify-center mt-15">
      <ul v-for = "(list,index) in myLikeProductList" :key = "index"
          style = "list-style: none">
        <li id = "myLikeListDiv">
          <div class = "p-5 mb-5 rounded float-left"
               style = "width: 300px; height: 400px; border: 2px solid green">
            <div class = "card-body">
              <span class = "mt-3" style = "margin-left: 130px"><strong>{{ list.title }}</strong></span>
              <br>
              <span class = "float-left card-subtitle">
              <router-link
                  :to = "{path:'/profile',query:{nickname:list.nickname}}"
                  style = "text-decoration: none; color:black">
                <span class = "float-left ml-3 mt-1 mr-3"><small>작성자 : {{ list.nickname }}</small></span></router-link>
              <br>
              <span class = "float-left ml-3 mt-1 mr-3"> <small>작성일 : {{ displayedAt(list.createDate) }}</small></span>
              <br>
              <span class = "float-left ml-3 mt-1 mr-3"><small>지역 : {{ list.area }}</small></span>
              </span>
              <br>

              <div id = "myLikeProductListImgDiv" style = "height: 100%; width: 100%">
                <router-link
                    :to = "{
                    path:'/productDetail',
                    query:{productId:list.id}
                  }">
                  <v-img
                      :src = "list.thumbnail"
                      class = "mt-15 mr-3 ml-13 grey lighten-3"
                      height = "200"
                      width = "200">

                  </v-img>
                </router-link>
                <div
                    v-if = "list.tags.length ===0 "
                    style = "height: 34px">
                </div>

                <div v-else id = "myLikeProductTagDiv" class = "mt-5">
                  <v-row align-content = "center" justify = "center">
                    <div v-for = "(tags,index) in list.tags" :key = "index" style = "list-style: none; display: inline">
                      <v-chip
                          class = "ml-0 mr-1 pr-2 pl-2"
                          color = "info"
                          label
                          small>
                        {{ tags.tag }}
                      </v-chip>
                    </div>
                  </v-row>
                </div>

                <div id = "myLikeProductListIconDiv" class = "mt-8">
                  <v-row align-content = "center" justify = "center">
                    <v-icon
                        color = "blue darken-4">
                      mdi-message-text
                    </v-icon>
                    <span class = "mt-1">{{ list.readCount }}</span>

                    <v-icon
                        color = "green">
                      mdi-image-multiple
                    </v-icon>
                    <span class = "mt-1">{{ list.productAlbumCount }}</span>

                    <v-btn
                        v-if = "list.like"
                        color = "pink"
                        dark
                        icon
                        @click = "pushLike(list.id)">

                      <v-icon dark>mdi-heart</v-icon>

                    </v-btn>

                    <v-btn
                        v-else
                        color = "grey"
                        dark
                        icon
                        @click = "pushLike(list.id)">

                      <v-icon dark>mdi-heart</v-icon>

                    </v-btn>
                  </v-row>
                </div>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </v-container>
</template>

<script>
import {mixinData} from "@/mixin/mixins";

export default {
  name    : "MyLikeProduct",
  mixins  : [mixinData],
  data() {
    return {
      paramNickname: ''
    }
  },
  methods : {
    async pushLike(productId) {
      await this.$store.dispatch('REQUEST_PUSH_INTEREST', productId)
          .then(() => {
            this.$store.dispatch('REQUEST_GET_MY_LIKE_PRODUCT', this.nickname);
          });
    },
  },
  computed: {
    myLikeProductList() {
      return this.$store.state.productStore.myLikeProductList;
    },
    nickname() {
      return this.$store.state.memberStore.nickname;
    }
  },
  created() {
    this.paramNickname = this.$route.query.nickname;
  },
  mounted() {
    this.$store.dispatch('REQUEST_GET_MY_LIKE_PRODUCT', this.paramNickname);
  }
}
</script>

<style scoped>

</style>