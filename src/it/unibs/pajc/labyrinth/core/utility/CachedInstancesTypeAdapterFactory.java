package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

// TODO: implement or delete
// https://stackoverflow.com/questions/58841054/gson-same-object-referenced-in-two-classes-duplicated-instance-after-decode
// https://stackoverflow.com/questions/11271375/gson-custom-seralizer-for-one-variable-of-many-in-an-object-using-typeadapter
public class CachedInstancesTypeAdapterFactory implements TypeAdapterFactory {

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }
}
