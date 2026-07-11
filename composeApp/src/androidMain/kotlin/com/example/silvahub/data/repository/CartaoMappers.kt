package com.example.silvahub.data.repository

import com.example.silvahub.data.local.entity.CartaoEntity
import com.example.silvahub.data.local.entity.CompraCartaoEntity
import com.example.silvahub.data.local.entity.FaturaEntity
import com.example.silvahub.data.local.entity.PagamentoFaturaEntity
import com.example.silvahub.data.local.entity.ParcelaCartaoEntity
import com.example.silvahub.data.local.entity.RecorrenciaCartaoEntity
import com.example.silvahub.domain.model.Cartao
import com.example.silvahub.domain.model.CompraCartao
import com.example.silvahub.domain.model.ECategoriaGasto
import com.example.silvahub.domain.model.EStatusFatura
import com.example.silvahub.domain.model.ETipoCompraCartao
import com.example.silvahub.domain.model.Fatura
import com.example.silvahub.domain.model.PagamentoFatura
import com.example.silvahub.domain.model.ParcelaCartao
import com.example.silvahub.domain.model.RecorrenciaCartao

fun CartaoEntity.toDomain() = Cartao(
    id = id,
    limiteCentavos = limiteCentavos,
    diaFechamento = diaFechamento,
    diaVencimento = diaVencimento,
    dataCriacao = dataCriacao,
)

fun Cartao.toEntity() = CartaoEntity(
    id = id,
    limiteCentavos = limiteCentavos,
    diaFechamento = diaFechamento,
    diaVencimento = diaVencimento,
    dataCriacao = dataCriacao,
)

fun FaturaEntity.toDomain() = Fatura(
    id = id,
    cartaoId = cartaoId,
    mesReferencia = mesReferencia,
    dataFechamento = dataFechamento,
    dataVencimento = dataVencimento,
    valorPagoCentavos = valorPagoCentavos,
    status = EStatusFatura.valueOf(status),
)

fun Fatura.toEntity() = FaturaEntity(
    id = id,
    cartaoId = cartaoId,
    mesReferencia = mesReferencia,
    dataFechamento = dataFechamento,
    dataVencimento = dataVencimento,
    valorPagoCentavos = valorPagoCentavos,
    status = status.name,
)

fun CompraCartaoEntity.toDomain() = CompraCartao(
    id = id,
    cartaoId = cartaoId,
    recorrenciaId = recorrenciaId,
    mesReferenciaCobranca = mesReferenciaCobranca,
    descricao = descricao,
    valorTotalCentavos = valorTotalCentavos,
    categoria = ECategoriaGasto.valueOf(categoria),
    data = data,
    tipo = ETipoCompraCartao.valueOf(tipo),
    totalParcelas = totalParcelas,
    estornada = estornada,
    dataCriacao = dataCriacao,
)

fun CompraCartao.toEntity() = CompraCartaoEntity(
    id = id,
    cartaoId = cartaoId,
    recorrenciaId = recorrenciaId,
    mesReferenciaCobranca = mesReferenciaCobranca,
    descricao = descricao,
    valorTotalCentavos = valorTotalCentavos,
    categoria = categoria.name,
    data = data,
    tipo = tipo.name,
    totalParcelas = totalParcelas,
    estornada = estornada,
    dataCriacao = dataCriacao,
)

fun ParcelaCartaoEntity.toDomain() = ParcelaCartao(
    id = id,
    compraId = compraId,
    faturaId = faturaId,
    numeroParcela = numeroParcela,
    valorCentavos = valorCentavos,
)

fun ParcelaCartao.toEntity() = ParcelaCartaoEntity(
    id = id,
    compraId = compraId,
    faturaId = faturaId,
    numeroParcela = numeroParcela,
    valorCentavos = valorCentavos,
)

fun RecorrenciaCartaoEntity.toDomain() = RecorrenciaCartao(
    id = id,
    cartaoId = cartaoId,
    descricao = descricao,
    valorCentavos = valorCentavos,
    categoria = ECategoriaGasto.valueOf(categoria),
    diaCobranca = diaCobranca,
    ativa = ativa,
    dataInicio = dataInicio,
    dataCancelamento = dataCancelamento,
)

fun RecorrenciaCartao.toEntity() = RecorrenciaCartaoEntity(
    id = id,
    cartaoId = cartaoId,
    descricao = descricao,
    valorCentavos = valorCentavos,
    categoria = categoria.name,
    diaCobranca = diaCobranca,
    ativa = ativa,
    dataInicio = dataInicio,
    dataCancelamento = dataCancelamento,
)

fun PagamentoFaturaEntity.toDomain() = PagamentoFatura(
    id = id,
    faturaId = faturaId,
    valorCentavos = valorCentavos,
    data = data,
    estornado = estornado,
    dataEstorno = dataEstorno,
    dataCriacao = dataCriacao,
)

fun PagamentoFatura.toEntity() = PagamentoFaturaEntity(
    id = id,
    faturaId = faturaId,
    valorCentavos = valorCentavos,
    data = data,
    estornado = estornado,
    dataEstorno = dataEstorno,
    dataCriacao = dataCriacao,
)
