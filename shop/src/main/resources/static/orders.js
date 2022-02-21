let stompClient;

$(() => {
    let socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (__) => {

        stompClient.subscribe('/order-status', (orderStatus) => {
            let body = JSON.parse(orderStatus['body'])

            let orderStatusFormatted = orderStatusFormat(body);

            let $existingOrderDiv = $(`#${body['id']}`);
            if ($existingOrderDiv.length) {
                $existingOrderDiv.text(orderStatusFormatted);
                return
            }

            let $orderStatusContainer = $('#order-statuses-container');
            $orderStatusContainer.append(`<div id="${body['id']}">${orderStatusFormatted}</div>`)
        });
    });
})

function orderStatusFormat(body) {
    return `Order id: ${body['id']}, status: ${body['status']}`
}