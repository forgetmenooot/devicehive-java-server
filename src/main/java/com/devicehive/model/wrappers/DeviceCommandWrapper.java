package com.devicehive.model.wrappers;

import com.devicehive.json.strategies.JsonPolicyDef;
import com.devicehive.model.HiveEntity;
import com.devicehive.model.JsonStringWrapper;
import com.google.gson.annotations.SerializedName;

import java.util.Optional;

import static com.devicehive.json.strategies.JsonPolicyDef.Policy.*;

/**
 * Created by tatyana on 2/12/15.
 */
public class DeviceCommandWrapper implements HiveEntity {
    private static final long serialVersionUID = 1179387574631106725L;

    @SerializedName("command")
    @JsonPolicyDef({COMMAND_FROM_CLIENT, COMMAND_TO_DEVICE, COMMAND_UPDATE_TO_CLIENT, COMMAND_UPDATE_FROM_DEVICE,
            POST_COMMAND_TO_DEVICE, COMMAND_LISTED})
    private Optional<String> command;

    @SerializedName("parameters")
    @JsonPolicyDef({COMMAND_FROM_CLIENT, COMMAND_TO_DEVICE, COMMAND_UPDATE_TO_CLIENT, COMMAND_UPDATE_FROM_DEVICE,
            POST_COMMAND_TO_DEVICE, COMMAND_LISTED, REST_COMMAND_UPDATE_FROM_DEVICE})
    private Optional<JsonStringWrapper> parameters;

    @SerializedName("lifetime")
    @JsonPolicyDef({COMMAND_FROM_CLIENT, COMMAND_TO_DEVICE, COMMAND_UPDATE_TO_CLIENT, COMMAND_UPDATE_FROM_DEVICE,
            COMMAND_LISTED, REST_COMMAND_UPDATE_FROM_DEVICE})
    private Optional<Integer> lifetime;

    @SerializedName("status")
    @JsonPolicyDef({COMMAND_FROM_CLIENT, COMMAND_TO_DEVICE, COMMAND_UPDATE_TO_CLIENT, COMMAND_UPDATE_FROM_DEVICE,
            POST_COMMAND_TO_DEVICE, REST_COMMAND_UPDATE_FROM_DEVICE, COMMAND_LISTED})
    private Optional<String> status;

    @SerializedName("result")
    @JsonPolicyDef({COMMAND_FROM_CLIENT, COMMAND_TO_DEVICE, COMMAND_UPDATE_TO_CLIENT, COMMAND_UPDATE_FROM_DEVICE,
            POST_COMMAND_TO_DEVICE, REST_COMMAND_UPDATE_FROM_DEVICE, COMMAND_LISTED})
    private Optional<JsonStringWrapper> result;

    public Optional<String> getCommand() {
        return command;
    }

    public void setCommand(Optional<String> command) {
        this.command = command;
    }

    public Optional<JsonStringWrapper> getParameters() {
        return parameters;
    }

    public void setParameters(Optional<JsonStringWrapper> parameters) {
        this.parameters = parameters;
    }

    public Optional<Integer> getLifetime() {
        return lifetime;
    }

    public void setLifetime(Optional<Integer> lifetime) {
        this.lifetime = lifetime;
    }

    public Optional<String> getStatus() {
        return status;
    }

    public void setStatus(Optional<String> status) {
        this.status = status;
    }

    public Optional<JsonStringWrapper> getResult() {
        return result;
    }

    public void setResult(Optional<JsonStringWrapper> result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceCommandWrapper that = (DeviceCommandWrapper) o;

        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (lifetime != null ? !lifetime.equals(that.lifetime) : that.lifetime != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (result != null ? !result.equals(that.result) : that.result != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = command != null ? command.hashCode() : 0;
        result1 = 31 * result1 + (parameters != null ? parameters.hashCode() : 0);
        result1 = 31 * result1 + (lifetime != null ? lifetime.hashCode() : 0);
        result1 = 31 * result1 + (status != null ? status.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "DeviceCommandWrapper{" +
                "command='" + command + '\'' +
                ", parameters=" + parameters +
                ", lifetime=" + lifetime +
                ", status='" + status + '\'' +
                ", result=" + result +
                '}';
    }
}
