<template>
    <tr>
      <td>{{payment.paymentid}}</td>
      <td>{{payment.walletid}}</td>
      <td>{{payment.type}}</td>
      <td>{{payment.summary}}</td>
      <td>{{payment.date}}</td>
    </tr>
</template>
<script>
export default {
    props: {
        payment: {
            type: Object,
            required: true
        }
    }
}
</script>