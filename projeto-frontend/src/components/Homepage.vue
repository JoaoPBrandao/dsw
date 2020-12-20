<template lang="html">
  <div>
    <div class="login">
      <div class="row jumbotron-row">
        <div class="col-md-12">
          <div v-if="$root.credentials && !this.loading && quantidade > 0" class="row">
            <div class="col-sm-12">
              <div class="alert alert-warning" role="alert">
                Você tem {{quantidade}} compartilhamentos em aberto!
                <button type="button" @click="quantidade = 0" class="close" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
            </div>
          </div>
          <div class="jumbotron border text-center">
            <h1>Shared4U</h1>
            <p class="lead">
              Comece a compartilhar seus itens hoje mesmo!
            </p>

            <p v-if="!$root.credentials">
              <router-link class="link" :to="{ name: 'login' }">
                <button class="btn btn-lg btn-primary" role="button">Login</button>
              </router-link>
              <router-link class="link" :to="{ name: 'create-account' }">
                <button class="btn btn-lg btn-primary" role="button">Cadastro</button>
              </router-link>
            </p>
          </div>
        </div>
      </div>
  
      <div class="row marketing text-center">
        <div class="col-md-4">
          <img width="70px" height="70px" src="../assets/clock.svg">
          <h3>Rápido</h3>
          <p>
            Você só precisa criar uma conta, e já pode começar a compartilhar seus itens!
          </p>
        </div>

        <div class="col-md-4">
          <img width="70px" height="70px" src="../assets/easy-use.svg">
          <h3>Fácil</h3>
          <p>
            Após criar uma conta, você só precisa cadastrar seu item, inserindo nome e descrição, e pode começar a
            compartilhar com outros usuários.
          </p>
        </div>
        <div class="col-md-4">
          <img width="70px" height="70px" src="../assets/shield.svg">
          <h3>Seguro</h3>
          <p>
            Nossa plataforma conta com verificações de segurança para garantir que você não será prejudicado por
            atitudes maliciosas de outros usuários.
          </p>
        </div>
      </div>
    </div>

    <div class="row footer-row">
      <div class="col-md-12">
        <div class="footer">
          <p>©2020 UNIRIO, Universidade Federal do Estado do Rio de Janeiro</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
    data() {
      return {
        quantidade: 0,
        loading: false,
        httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
        }
      }
    },
    mounted(){
      if(this.$root.credentials){
        this.loading = true;
        axios.get(`/api/compartilhamento/quantidadeAberto`, this.httpOptions)
        .then(response => {
          this.loading = false;
          this.quantidade = response.data.data;
        })
        .catch(err => {
          this.loading = false;
          console.log(err);
        })
      }
    }
  }
</script>

<style lang="css" scoped>
div.jumbotron-row {
  margin-top: 32px;
}
.marketing h4 {
  font-size: 20px;
}
.footer {
    margin-top: 64px;
    margin-bottom: 32px;
    padding-top: 8px;
    color: #777;
    border-top: 1px solid #e5e5e5;
}
  .jumbotron {
    background-image: url("https://i.imgur.com/k8v5IIo.jpg");
    background-size: cover;
    border: 1px outset #a2a2a2;
  }
</style>
