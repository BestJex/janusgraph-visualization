<template>
	<div class="gremlin">
		<div>
			<el-card class="box-card">
				<div class="text item" style="padding-bottom: 5px;">
					<el-input type="textarea" v-model="gremlin" rows="5" resize="none"></el-input>
				</div>
				<el-row>
					<div>
						<el-col :span="4">
							<div>
								<el-input v-model="host" size="small">
									<template slot="prepend">地址</template>
								</el-input>
							</div>
						</el-col>
						<el-col :span="4">
							<div>
								<el-input v-model="port" size="small">
									<template slot="prepend">端口</template>
								</el-input>
							</div>
						</el-col>
						<el-col :span="4">
							<div>
								<el-input v-model="sourceName" size="small">
									<template slot="prepend">SourceName</template>
								</el-input>
							</div>
						</el-col>
						<el-col :span="1" :offset="10">
							<div>
								<el-button size="small" style="margin-top: 5px;" v-on:click="query" type="primary">执行</el-button>
							</div>
						</el-col>
					</div>
				</el-row>
			</el-card>
		</div>
		<div style="padding-top: 5px;">
			<el-col>
				<el-col :span="6">
					<el-card class="box-card" id="result">
						<div slot="header" class="clearfix">
							<span>查询结果</span>
						</div>
						<div class="text item" v-html="gremlinResult" id="result-data">
						</div>
					</el-card>
				</el-col>
				<el-col :span="18" style="padding-left: 5px;">
					<el-card id="graph" class="box-card">

					</el-card>
				</el-col>
			</el-col>
		</div>
		<div>
			<a style="padding-left: 30px;" href="https://github.com/fenglex/janusgraph-visualization">使用说明https://github.com/fenglex/janusgraph-visualization</a>
		</div>
	</div>

</template>

<script>
	import axios from 'axios'
	import vis from 'vis-network'
	import {
		Notification
	} from 'element-ui';
	export default {
		name: 'gremlin',
		data: function() {
			return {
				name: 'haifeng',
				host: 'localhost',
				port: '8182',
				sourceName: 'g',
				gremlin: '',
				gremlinResult: ''
			};
		},
		mounted: function() {
			let wh = document.documentElement.clientHeight;
			let eh = 247;
			let ch = (wh - eh) + "px";
			document.getElementById("result").style.minHeight = ch;
			let c = 247;
			let gh = (wh - c) + "px";
			document.getElementById("graph").style.height = gh;
		},
		methods: {
			query: function() {
				if (this.$data.gremlin.length < 3) {
					return;
				}
				this.$data.gremlinResult = "";
				axios({
						url: this.$base_url + '/query',
						params: {
							host: this.$data.host,
							port: this.$data.port,
							gremlin: this.$data.gremlin,
							sourceName: this.$data.sourceName
						}
					}).then(res => {
						var result = res.data;
						this.$data.gremlinResult = result.result.replace(/\n/g, "<br/>");
						// create a network
						var _this = this;
						var container = document.getElementById('graph');
						var nodes = new vis.DataSet(result.vertices);
						var edges = new vis.DataSet(result.edges);
						// provide the data in the vis format
						var data = {
							nodes: nodes,
							edges: edges
						};
						var options = {
							edges: {
								arrows: {
									to: {
										type: "arrow",
										enabled: true
									}
								}
							},
							nodes: {
								shape: 'circle'
							},
							interaction: {
								selectConnectedEdges: false
							}
						};
						var network = new vis.Network(container, data, options);
						network.on("selectNode", function(params) {
							var nid = params.nodes[0];
							Notification.closeAll();
							var node = nodes._data.get(nid);
							var title = "id:" + node.id + ",\tlabel:" + node.label;
							axios({
								url: _this.$base_url + '/vertex',
								params: {
									host: _this.$data.host,
									port: _this.$data.port,
									id: nid,
									sourceName: _this.$data.sourceName
								}
							}).then(res => {
								var result = res.data;
								var c = "";
								var values = result.keyValues;
								for (var i = 0; i < values.length; i++) {
									c += values[i].key + ":&emsp;" + values[i].value + "<br/>";
								}
								Notification({
									title: title,
									message: c,
									dangerouslyUseHTMLString: true,
									duration: 60000,
									customClass: 'prop-box',
									position: 'bottom-right'
								});
							}).catch(function() {});
						});
						network.on("selectEdge", function(params) {
							var eid = params.edges[0];
							Notification.closeAll();
							var edge = edges._data.get(eid);
							var title = "id:" + edge.id + ",\tlabel:" + edge.label;
							axios({
								url: _this.$base_url + '/edge',
								params: {
									host: _this.$data.host,
									port: _this.$data.port,
									id: eid,
									sourceName: _this.$data.sourceName
								}
							}).then(res => {
								var result = res.data;
								var c = "";
								var values = result.keyValues;
								for (var i = 0; i < values.length; i++) {
									c += values[i].key + ":&emsp;" + values[i].value + "<br/>";
								}
								Notification({
									title: title,
									message: c,
									dangerouslyUseHTMLString: true,
									duration: 60000,
									customClass: 'prop-box',
									position: 'bottom-right'
								});
							}).catch(function() {});
						});
					})
					.catch(function() {

					});
			}
		}
	}
</script>

<style>
	.prop-box {
		min-width: 150px;
	}

	.el-card__header {
		padding: 5px 10px;
	}

	#result-data {
		font-size: 12px;
	}
</style>
