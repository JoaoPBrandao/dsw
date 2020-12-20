<template lang="html">
  <div class="lista-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-12 text-left">
      <div>
        <div class="header">
          <h2 class="form-title">Itens Compartilhados</h2>
          <h6 class="form-subtitle">Abaixo estão os seus itens compartilhados.</h6>
        </div>
        <div class="new-button">
          <button type="button" class="btn btn-primary" @click="novo">Novo Item</button>
        </div>
        <div class="clear"></div>
      </div>

        <div class="row">
          <div class="col-sm-11">
            <div class="btn-group">
              <input id="searchinput" v-model="search" type="search" class="form-control">
              <span @click="() => {search = ''; processForm()}" id="searchclear" class="glyphicon glyphicon-remove-circle"></span>
            </div>
          </div>
          <div class="col-sm-1">
            <button style="float: right" class="btn btn-primary" @click="processForm()">Buscar</button>
          </div>
        </div>

      <table class="table table-striped table-hover" id="tbItens">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Descrição</th>
          <th>Tipo</th>
          <th class="commands"></th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in items">
          <td>{{item.nome}}</td>
          <td>{{item.descricao}}</td>
          <td>{{item.tipo}}</td>
          <td style="width: fit-content">
            <RouterLink class="glyphicon glyphicon-eye-open link" :to="{name: 'item-view', params: {item: item}}" />
            <RouterLink class="glyphicon glyphicon-pencil link" :to="{name: 'item-update', params: {item: item}}" />
            <RouterLink class="glyphicon glyphicon-remove link" :to="{name: 'item-delete', params: {item: item}}" />
          </td>
        </tr>
      </tbody>
      </table>

      <div>
        <div class="page-item first" :class="{ disable: this.page == 1 }" @click="moveTo(page-1)" @>&lt;&lt;</div>
        <div class="page-item" v-show="page > 3" @click="moveTo(page-3)">{{page-3}}</div>
        <div class="page-item" v-show="page > 2" @click="moveTo(page-2)">{{page-2}}</div>
        <div class="page-item" v-show="page > 1" @click="moveTo(page-1)">{{page-1}}</div>
        <div class="page-item current disable">{{page}}</div>
        <div class="page-item" v-show="totalPages > page"   @click="moveTo(page+1)">{{page+1}}</div>
        <div class="page-item" v-show="totalPages > page+1" @click="moveTo(page+2)">{{page+2}}</div>
        <div class="page-item" v-show="totalPages > page+2" @click="moveTo(page+3)">{{page+3}}</div>
        <div class="page-item last" :class="{ disable: this.page == this.totalPages }" @click="moveTo(page+1)">&gt;&gt;</div>
        <div class="clear"></div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      page: 1,
      totalPages: 1,
      items: [],
      search: "",
      errors: null,

      httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
      },
    }
  },

  created: function () {
    this.processForm();
  },  

  methods: {
    processForm: function() {
      axios.get(`/api/item/lista?sort=&per_page=10&filter=${this.search}&page=${this.page}`, this.httpOptions)
        .then(response => {
          this.items = response.data.data.data;
          this.page = response.data.data.current_page;
          this.totalPages = response.data.data.last_page;
          this.errors = null;
        })
        .catch(error => {
          this.errors = error.response.data.errors;
        });
    },

    moveTo: function(page) {
      if (page < 1)
        page = 1;

      if (page > this.totalPages)
        page = this.totalPages;

      this.page = page;
      this.processForm();
    },

    novo: function() {
      this.$router.push({ name: 'item-new' });
    }
  }
}
</script>

<style lang="css" scoped>
div.lista-items-compartilhados {
  margin-top: 32px;
}
th.commands {
  width: 72px;
}
div.page-item {
  color: #2973b7;
  text-decoration: none;
  cursor: pointer;
  padding: 10px;
  border: 1px solid lightgray;
  margin: 0px -1px 0px 0px;
  float: left;
}
div.page-item.first {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}
div.page-item.last {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}
div.page-item.disable {
  color: gray;
  cursor: auto;
}
div.page-item.current {
  background-color: lightgray;
}
div.clear {
  clear: both;
}
div.header {
  float: left;
}
div.new-button {
  float: right;
  text-align: right;
}
#searchinput {
  width: 100%;
}
.btn-group {
  width: 100%;
}
#searchclear {
  position: absolute;
  right: 5px;
  top: 0;
  bottom: 0;
  height: 14px;
  margin: auto;
  font-size: 14px;
  cursor: pointer;
  color: #ccc;
}
</style>