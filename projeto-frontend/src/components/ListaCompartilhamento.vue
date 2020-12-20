<template>
    <div class="lista-compartilhamentos row" v-if="this.$root.credentials">
        <div class="col-md-12 text-left">
            <div>
                <div class="header">
                    <h2 class="form-title">Compartilhamentos Recebidos</h2>
                    <h6 class="form-subtitle">Abaixo estão os compartilhamentos que você recebeu.</h6>
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

            <table class="table table-striped table-hover" id="tbCompartilhamentos">
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Usuário</th>
                    <th>Início</th>
                    <th>Término</th>
                    <th class="status">Status</th>
                    <th class="commands"></th>
                </tr>
                </thead>

                <tbody>
                <tr v-for="compartilhamento in compartilhamentos">
                    <td>{{compartilhamento.item_nome}}</td>
                    <td>{{compartilhamento.usuario_nome}}</td>
                    <td>{{compartilhamento.data_inicio}}</td>
                    <td>{{compartilhamento.data_termino}}</td>
                    <td>{{compartilhamento.status}}</td>
                    <td style="width: fit-content">
                        <a data-toggle="modal" @click="selecionado = compartilhamento.id" data-target="#modalAceitar" v-if="compartilhamento.status == 'Aberto'"><i class="fas fa-check-circle"></i></a>
                        <a data-toggle="modal" @click="selecionado = compartilhamento.id" data-target="#modalRejeitar" v-if="((compartilhamento.status == 'Aberto') || (compartilhamento.status == 'Aceito'))"><i class="fas fa-times-circle"></i></a>
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

        <!-- MODAL PARA ACEITAR O COMPARTILHAMENTO -->
        <div id="modalAceitar" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title">Aceitar Compartilhamento</h2>
                        <button type="button" @click="() => {success = false; errors = null}" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <p>Tem certeza que deseja Aceitar o compartilhamento?</p>
                    </div>
                    <div class="modal-footer">
                        <button @click="aceitarCompartilhamento" class="btn btn-success">Sim</button>
                        <button class="btn btn-primary" data-dismiss="modal">Não</button>
                        <div v-if="success" class="alert alert-success" role="alert">
                            Compartilhamento aceito com sucesso!
                            <button type="button" @click="success = false" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div v-if="errors" class="alert alert-danger" role="alert">
                            Erro ao aceitar compartilhamento: {{errors.message}}
                            <button type="button" @click="errors = null" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- MODAL PARA REJEITAR O COMPARTILHAMENTO -->
        <div id="modalRejeitar" class="modal fade" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title">Rejeitar Compartilhamento</h2>
                        <button type="button" @click="() => {success = false; errors = null}" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <p>Tem certeza que deseja Rejeitar o compartilhamento?</p>
                    </div>
                    <div class="modal-footer">
                        <button @click="rejeitarCompartilhamento" class="btn btn-danger">Sim</button>
                        <button class="btn btn-primary" data-dismiss="modal">Não</button>
                        <div v-if="success" class="alert alert-success" role="alert">
                            Compartilhamento rejeitado com sucesso!
                            <button type="button" @click="success = false" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div v-if="errors" class="alert alert-danger" role="alert">
                            Erro ao rejeitar compartilhamento: {{errors.message}}
                            <button type="button" @click="errors = null" class="close" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        created() {
            this.processForm();
        },
        name: "ListaCompartilhamento",
        data() {
            return {
                page: 1,
                totalPages: 1,
                selecionado: null,
                compartilhamentos: [],
                httpOptions: {
                    baseURL: this.$root.config.url,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + this.$root.credentials.token
                    }
                },
                search: "",
                success: false,
                errors: null
            }
        },
        methods: {
            processForm: function() {
                this.page = 1;
                this.updateList();
            },
            updateList() {
                axios.get(`/api/compartilhamento/listaUsuario?sort=&per_page=10&filter=${this.search}&page=${this.page}`, this.httpOptions)
                    .then(response => {
                        this.compartilhamentos = response.data.data.data;
                        this.page = response.data.data.current_page;
                        this.totalPages = response.data.data.last_page;
                        this.error = null;
                    })
                    .catch(error => {
                        this.errors = error.response.data.errors;
                        this.success = false;
                    });
            },
            rejeitarCompartilhamento(){
                axios.post(`/api/compartilhamento/rejeitar/${this.selecionado}`,{}, this.httpOptions)
                    .then(() => {
                        this.success = true;
                        this.errors = null;
                        this.updateList();
                    })
                    .catch(err => {
                        this.errors = err.response.data.errors;
                        this.success = false;
                    })
            },
            aceitarCompartilhamento(){
                axios.post(`/api/compartilhamento/aceitar/${this.selecionado}`,{}, this.httpOptions)
                .then(() => {
                    this.success = true;
                    this.errors = null;
                    this.updateList();
                })
                .catch(err => {
                    this.errors = err.response.data.errors;
                    this.success = false;
                })
            },
            moveTo: function(page) {
                if (page < 1)
                    page = 1;

                if (page > this.totalPages)
                    page = this.totalPages;

                this.page = page;
                this.updateList();
            }
        }
    }
</script>

<style scoped>
    div.lista-compartilhamentos {
        margin-top: 32px;
    }
    th.commands {
        width: 48px;
    }
    a{
        cursor: pointer;
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
    th {
        text-align: center;
    }
    td {
        text-align: center;
    }
    .alert {
        margin-top: 10px;
        margin-bottom: 0px !important;
    }
    .modal h2 {
        display: inline-block;
    }
    .modal-footer .alert {
        text-align: left;
    }
</style>