<template>
  <div id="app">
    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <router-link class="link" to="/">
            <span class="navbar-brand">Shared4U</span>
          </router-link>
        </div>

        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-left">
            <li v-if="$root.credentials">
              <router-link class="link" :to="{ name: 'item-list' }">
                <i class="fas fa-list"></i>
                Itens
              </router-link>
            </li>
            <li v-if="$root.credentials">
              <router-link class="link" :to="{ name: 'compartilhamento-list' }">
                <i class="fas fa-address-book"></i>
                Compartilhamentos recebidos
              </router-link>
            </li>
          </ul>

          <ul class="nav navbar-nav navbar-right">
            <li class="user-commands" v-if="$root.credentials">
              <span class="greetings">Olá, {{$root.credentials.nome}}</span>
              <router-link class="link" :to="{ name: 'change-password' }" replace>Troca senha</router-link>
              <a class="link" @click="logout">Logout</a>
            </li>

            <li class="login-commands" v-if="!$root.credentials">
              <router-link class="link" :to="{ name: 'login' }">Login</router-link>
              <router-link class="link" :to="{ name: 'create-account' }">Criar conta</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <div class="container">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
  export default {
    data () {
      return { }
    },

    methods: {
      logout: function() {
        this.$root.credentials = null;
        this.$router.replace('/');
      }
    }
  }
</script>

<style>
#app {
  font-family: Helvetica, Arial, sans-serif;
  padding-top: 60px;
}
span.greetings {
  color: white;
  margin: 0 4px;
}
li {
  margin: 0 4px;
}
li.login-commands {
  color: white;
  padding-top: 15px;
  margin-right: 32px;
}
li.login-commands a {
  display: inline;
  padding: 15px 8px;
}
li.user-commands {
  color: white;
  padding-top: 15px;
  margin-right: 32px;
}
li.user-commands span.greetings {
  margin-right: 12px;
}
li.user-commands a {
  display: inline;
  padding: 15px 8px;
}
.link{
  cursor: pointer;
}
</style>
