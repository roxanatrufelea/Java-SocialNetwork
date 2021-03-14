package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    /***
     * Reads and loads the data from the file, in memory
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    /**
     *
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     */
    @Override
    public E save(E entity){
        E e=super.save(entity);
        if (e==null)
        {
            writeToFile(entity);
        }
        return e;

    }

    /***
     * Removes the content of the given file
     * @throws IOException, if the file can't be opened
     */
    protected void removeFileContent() throws IOException {
        try {
            FileWriter file = new FileWriter(fileName);
            file.write("");
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /***
     * Deletes the object with the given id from the memory and from file
     * @param id:ID
     * @return e: the removed element
     */
    public E delete(ID id)  {
        E e=super.delete(id);
        if(e!=null){
            try {
                this.removeFileContent();
                this.writeFile();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return e;
    }

    /***
     * Writes the given object from memory to file
     * @param entity:E
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * Writes all objects from memory to file
     */
    protected void writeFile(){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))){

          for (ID e:entities.keySet()){
              bW.write(createEntityAsString(entities.get(e)));
              bW.newLine();
          }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

